package survivor.agents;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;

import java.util.Hashtable;

import survivor.SurvivorProperties;
import survivor.ontology.Location;
import survivor.ontology.Person;
import survivor.ontology.Resource;
import survivor.ontology.ResourceIsLocated;
import survivor.ontology.actions.RequestSeenResources;
import survivor.ontology.actions.RequestSeenResourcesResponse;
import survivor.ontology.actions.SendAgentInfo;
import survivor.ontology.actions.SendFoundResources;

public class Seeker extends AbstractAgent{

	private static final long serialVersionUID = 1L;
	
	Hashtable<Location, Boolean> knownResources = new Hashtable<Location, Boolean>();
	Hashtable<Location, Resource> foundResources = new Hashtable<Location, Resource>();
	
	public Seeker() {
		super();
		this.energy = SurvivorProperties.MAX_ENERGY;
	}
	

	@Override
	public void sendAgentInfo()
	{
		
		Person agentInfo = new Person();
		
		agentInfo.setPersonLocation(this.currentPos);
		agentInfo.setEnergy(this.energy);
		agentInfo.setType(Person.SEEKER);
		
		SendAgentInfo sendInfo = new SendAgentInfo();
		sendInfo.setPerson(agentInfo);
		
		sendMessage(ACLMessage.INFORM, sendInfo, this.world);
		
		if (this.energy <= 0)
			doDelete();
	}

	
	protected void setup(){
		
		this.world = (AID) getArguments()[0];
		this.base = (AID) getArguments()[1];
		this.baseLocation = (Location) getArguments()[2];
		
		Location newDestination = new Location();
		newDestination.setPosx((int)(Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2));
		newDestination.setPosy((int)(Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2));
		this.destination = newDestination;
		System.out.println("Seeker world is " + this.world);
		System.out.println("Agent " + this.getLocalName() + " setup... timestamp: " + System.currentTimeMillis());
		
		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("Seeker"); 
		sd.setName(getName());
		sd.setOwnership("SURVIVOR");
		
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		sd = new ServiceDescription();   
		sd.setType("tick"); 
		sd.setName(getName());
		sd.setOwnership("SURVIVOR");
		
		dfd.addServices(sd);
		
		
		try {
		    DFService.register(this,dfd);
		} catch (FIPAException e) {
		    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
		    doDelete();
		}
		
		this.currentPos = this.baseLocation.clone();
		this.setEnergy(SurvivorProperties.MAX_ENERGY);
		
		addBehaviour(new SeekerBehaviour(this));
		
	}
	
	public void setKnownResources()
	{
		RequestSeenResources request = new RequestSeenResources();
		
		sendMessage(ACLMessage.REQUEST, request, this.base);
		
		ACLMessage reply = blockingReceive();
		
		if (reply != null)
		{
			ContentElement content;
			try {
				content = getContentManager().extractContent(reply);
				Concept action = ((Action)content).getAction();
	            switch (reply.getPerformative()) {
	               case (ACLMessage.INFORM):	
						if (action instanceof RequestSeenResourcesResponse)
						{							
							RequestSeenResourcesResponse response = (RequestSeenResourcesResponse)action;
							ArrayList resources = response.getResource();							
							if (resources != null)
							{
								this.knownResources = new Hashtable<Location, Boolean>();
								for (int i = 0; i < resources.size(); i++)
								{
									Resource r = (Resource)resources.get(i);
									this.knownResources.put(r.getLocation(), true);
								}
							}
						}
	               break;
	            }
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void updateResources()
	{
		//TODO: implement update resources
		ArrayList array = new ArrayList();
		for (Resource r : foundResources.values())
			array.add(r);
		
		SendFoundResources msg = new SendFoundResources();
		msg.setResource(array);
	
		sendMessage(ACLMessage.INFORM, msg, base);

		this.foundResources = new Hashtable<Location, Resource>();
		
	}
	
	static class SeekerBehaviour extends CyclicBehaviour {
		
		private static final long serialVersionUID = 1L;
		
		

		public SeekerBehaviour(Agent a) {
			super(a);
		}
		
		void seekFoodArround()
		{
			Seeker a = ((Seeker)this.myAgent);
			int x=a.getCurrentPos().getPosx(), y=a.getCurrentPos().getPosy(), i, j;
			for (i=-2;i<=2;i++)
				for (j=-2;j<2;j++)
				{
					Location local = new Location();
					
					local.setPosx(x+i);
					local.setPosy(y+j);

					Resource res;
					if ( (res = a.getResourceOfLocation(local)) != null )
					{
						System.out.println("FOUND!");
						a.foundResources.put(local, res);
						a.setDestination(a.baseLocation);
					}
				}
			return;
		}

		@Override
		public void action() {
			int step = 1;
			Seeker agent = (Seeker)this.myAgent;
			
			ACLMessage msg = agent.blockingReceive();
			
			if ( msg != null )
			{
				if (msg.getContent().equals("work"))
				{
					agent.energy -= SurvivorProperties.TRAVEL_COST;
					
					//TODO work of Seeker
					System.out.println(this.myAgent.getName()+": " + msg.getContent());
					
					if ( agent.isAtBase() )
					{	
						agent.setKnownResources();
						if ( agent.foundResources.values().size() != 0 )
						{
							agent.updateResources();
							
							Location newDestination = new Location();
							newDestination.setPosx((int)(Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2));
							newDestination.setPosy((int)(Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2));
							agent.setDestination(newDestination);
							agent.sendAgentInfo();
							return;
						}
						
						// eat if not with energy
						if ( agent.getEnergy() < 3*SurvivorProperties.MAX_ENERGY/4 )
						{
							agent.eat();
							agent.sendAgentInfo();
							return;
						}
						
						// try to reproduce
						if ( Math.random() < SurvivorProperties.REPRODUCTION_PERCENTAGE )
						{
							agent.reproduce();
							agent.sendAgentInfo();
							return;
						}
						agent.move(step);
						agent.sendAgentInfo();
					}
					else
					{
						seekFoodArround();
						
						if ( ! agent.isAtDestination() )
							agent.move(step);
						else
						{
							Location newDestination = new Location();
							newDestination.setPosx((int)(Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2));
							newDestination.setPosy((int)(Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2));
							agent.setDestination(newDestination);
						}
						agent.sendAgentInfo();
					}
										
				}

			}
		}
	}
	
}

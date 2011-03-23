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
import survivor.SurvivorProperties;
import survivor.ontology.Food;
import survivor.ontology.Location;
import survivor.ontology.Person;
import survivor.ontology.Wood;
import survivor.ontology.actions.RequestResourceAmountOfLocation;
import survivor.ontology.actions.RequestResourceAmountOfLocationResponse;

public class Lumberjack extends Gatherer {

	private static final long serialVersionUID = 1L;
	
	public Lumberjack() {
		super();
		this.currentPos = new Location();
		this.currentPos.setPosx(0);
		this.currentPos.setPosy(0);
	}

	@Override
	public void gather() {
		// TODO implement gather
		lastPosition = currentPos.clone();
		RequestResourceAmountOfLocation request = new RequestResourceAmountOfLocation();
		request.setAmount(SurvivorProperties.MAX_WORKLOAD);
		request.setLocation(currentPos);
		
		sendMessage(ACLMessage.REQUEST, request, world);
		
		ACLMessage reply = blockingReceive();
		
		if (reply != null)
		{
			ContentElement content;
			try {
				content = getContentManager().extractContent(reply);
				Concept action = ((Action)content).getAction();
	            switch (reply.getPerformative()) {
	               case (ACLMessage.INFORM):	
						if (action instanceof RequestResourceAmountOfLocationResponse)
						{
							this.workLoad = ((RequestResourceAmountOfLocationResponse)action).getAmount();
							this.destination = baseLocation;
						}
	               break;
	            }
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

	
	protected void setup(){
		
		getContentManager().registerLanguage(codec);
	    getContentManager().registerOntology(ontology);
	    
		this.world = (AID) getArguments()[0];
		this.base = (AID) getArguments()[1];
		this.baseLocation = (Location) getArguments()[2];
		
		this.type = Person.LUMBERJACK;
		
		
		System.out.println("Lumberjack world is " + this.world);
		System.out.println("Agent " + this.getLocalName() + " setup... timestamp: " + System.currentTimeMillis());
		
		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("Lumberjack"); 
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
		
		addBehaviour(new LumberjackBehaviour(this));
		
	}
	
	static class LumberjackBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;
		
		public LumberjackBehaviour(Agent a) {
		    super(a);
		}

		@Override
		public void action() 
		{
			//TODO: check step value and code location
			int step = 1;
			
			Lumberjack agent = (Lumberjack) myAgent;
			ACLMessage msg = this.myAgent.blockingReceive();
		
			if ( msg != null )
			{
				if ( msg.getContent().equals("work") )
				{
					agent.energy -= SurvivorProperties.TRAVEL_COST;
					
					//TODO TEST: work of Lumberjack
					
					if ( agent.isAtBase() )
					{						
						if ( agent.workLoad != 0 || agent.lastPosition != null)
						{
							agent.depositResource();
							agent.sendAgentInfo();
							agent.lastPosition = null;
							return;
						}
						
						// eat if not with energy
						if ( agent.getEnergy() < SurvivorProperties.MAX_ENERGY/2 )
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
					
						agent.setResourceToGather(Wood.class, agent.currentPos, (agent.getEnergy()/SurvivorProperties.TRAVEL_COST - 20)/2);
						if (agent.getResourceToGather() != null)
						{
							agent.setDestination(agent.getResourceToGather().getLocation());
							agent.move(step);
							agent.sendAgentInfo();
							return;
						}
						
					}
					else
					{
						if ( ! agent.isAtDestination() )
							agent.move(step);
						else
							agent.gather();
						
						agent.sendAgentInfo();
					}
										
				}
			}
		}
	}
}
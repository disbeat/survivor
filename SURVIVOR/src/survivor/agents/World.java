package survivor.agents;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;

import java.util.Hashtable;

import survivor.SurvivorProperties;
import survivor.graph.survivorInterface;
import survivor.ontology.BaseInfo;
import survivor.ontology.Food;
import survivor.ontology.Location;
import survivor.ontology.Person;
import survivor.ontology.Resource;
import survivor.ontology.ResourceIsLocated;
import survivor.ontology.Stone;
import survivor.ontology.SurvivorOntology;
import survivor.ontology.Wood;
import survivor.ontology.actions.RequestResourceAmountOfLocation;
import survivor.ontology.actions.RequestResourceAmountOfLocationResponse;
import survivor.ontology.actions.RequestResourceOfLocation;
import survivor.ontology.actions.RequestResourceOfLocationResponse;
import survivor.ontology.actions.SendAgentInfo;
import survivor.ontology.actions.SendBaseInfo;

public class World extends Agent {

	private static final long serialVersionUID = 1L;
	Location basePosition;
	
	protected Codec codec = new SLCodec();
	protected Ontology ontology = SurvivorOntology.getInstance();
	
	Hashtable<Location, ResourceIsLocated> worldResources = new Hashtable<Location, ResourceIsLocated>();
	Hashtable<String, Person> agents = new Hashtable<String, Person>();
	BaseInfo base = new BaseInfo();
	private int worldSize = SurvivorProperties.WORLD_SIZE;
	
	public World() {
		super();
	}
	
	private void createAgent(String name, String aClass, Object[] args) throws StaleProxyException{
		jade.wrapper.AgentContainer c = getContainerController();
        jade.wrapper.AgentController a = c.createNewAgent( name, aClass, args );
        a.start();
  
	}
	
	
	public void updateAgentsStatus(  )
	{
		
	}
	
	protected void sendMessage(int performative, AgentAction action, AID destinatary) {

		ACLMessage msg = new ACLMessage(performative);
		msg.setLanguage(this.codec.getName());
		msg.setOntology(ontology.getName());

		try {
		   getContentManager().fillContent(msg, new Action(destinatary, action));
		   msg.addReceiver(destinatary);
		   send(msg);
		   System.out.println("sended message from " + this.getLocalName() + " to " + destinatary.getLocalName());
		}
    	catch (Exception ex) { ex.printStackTrace(); }
   }
	public int getRandomCoordinate()
	{
		return (int)( Math.random() * (SurvivorProperties.WORLD_SIZE - 1) - SurvivorProperties.WORLD_SIZE/2 );
	}
	
	protected void setup() {
		
		// Register language and ontology
	    getContentManager().registerLanguage(codec);
	    getContentManager().registerOntology(ontology);
	    
	    
		System.out.println( "WORLD AID: " + this.getAID() );

		for ( int i = 0; i < SurvivorProperties.NUM_RESOURCES; i++ )
		{
			Stone r = new Stone();
			r.setAmount(200);
			r.setType(SurvivorProperties.STONE);
			Location l = new Location();
			l.setPosx( getRandomCoordinate() );
			l.setPosy( getRandomCoordinate() );
			ResourceIsLocated rl = new ResourceIsLocated();
			rl.setLocation(l);
			rl.setResource(r);
			worldResources.put(l, rl);
			
			Food f = new Food();
			f.setAmount(200);
			f.setType(SurvivorProperties.FOOD);
			l = new Location();
			l.setPosx( getRandomCoordinate() );
			l.setPosy( getRandomCoordinate() );
			rl = new ResourceIsLocated();
			rl.setLocation(l);
			rl.setResource(f);
			worldResources.put(l, rl);
			
			Wood w = new Wood();
			w.setAmount(200);
			w.setType(SurvivorProperties.WOOD);
			l = new Location();
			l.setPosx( getRandomCoordinate() );
			l.setPosy( getRandomCoordinate() );
			rl = new ResourceIsLocated();
			rl.setLocation(l);
			rl.setResource(w);
			
			worldResources.put(l, rl);
		}
		basePosition = new Location();
		basePosition.setPosx(0);
		basePosition.setPosy(0);
		
		base.setLocation(basePosition);
		base.setFood(new Food(Base.INITIAL_FOOD_VALUE));
		base.setWood(new Wood(Base.INITIAL_WOOD_VALUE));
		base.setStone(new Stone(Base.INITIAL_STONE_VALUE));
		
		
		
		new Thread(new survivorInterface(base, worldResources, worldSize, agents)).start();
		
		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("World"); 
		sd.setName(getName());
		sd.setOwnership("SURVIVOR");
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		try {
		    DFService.register( this, dfd );
		} catch (FIPAException e) {
		    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
		    doDelete();
		}
		
		
		
	    try 
	    {
			createAgent( "Base", "survivor.agents.Base", new Object[] { getAID(), basePosition } );
		} catch (StaleProxyException e) 
		{
			e.printStackTrace();
		}
           
        
        
        try {
			createAgent( "TimeController", "survivor.agents.TimeController", null );
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
        
		addBehaviour(new WorldBehaviour());
		
	}
	
	static class WorldBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			
			ACLMessage msg = this.myAgent.blockingReceive();
			
			if (msg != null) 
			{	        
				try 
		        {
		            ContentElement content = this.myAgent.getContentManager().extractContent(msg);
		            Concept action = ((Action)content).getAction();
	
		            switch (msg.getPerformative()) {
	
		               case (ACLMessage.REQUEST):
	
		                  System.out.println("Request from " + msg.getSender().getLocalName());
	
		                  if (action instanceof RequestResourceOfLocation)
		                  {
		                	  RequestResourceOfLocation request = (RequestResourceOfLocation)action;
		                	  RequestResourceOfLocationResponse response = new RequestResourceOfLocationResponse();
		                	  
		                	  ResourceIsLocated resp = null;
		                	  
		                	  for (Location r: ((World)this.myAgent).worldResources.keySet())
		                	  	if (r.equals(request.getLocation()))
		                	  		resp = ((World)this.myAgent).worldResources.get(r);
		                	   
		                	  Resource r = null; 
		                	  if (resp != null)
		                	  {
		                		  System.out.println("HERE!");
		                		  r = resp.getResource();
		                		  r.setLocation(resp.getLocation());
		                	  }
		                	  response.setResource(r);
		                	  
		                	  ((World)this.myAgent).sendMessage(ACLMessage.INFORM, response, msg.getSender());		                	  
		                  }else if (action instanceof RequestResourceAmountOfLocation)
		                  {
		                	  RequestResourceAmountOfLocation request = (RequestResourceAmountOfLocation)action;
		                	  RequestResourceAmountOfLocationResponse response = new RequestResourceAmountOfLocationResponse();
		                	  
		                	  ResourceIsLocated resp = null;
		                	  
		                	  
		                	  for (Location r: ((World)this.myAgent).worldResources.keySet())
		                	  	if (r.equals(request.getLocation()))
		                	  	{
		                	  		resp = ((World)this.myAgent).worldResources.get(r);
		                	  		break;
		                	  	}
		                	   
		                	  Resource r = null; 
		                	  if (resp != null)
		                	  {
		                		  
		                		  r = resp.getResource();
		                		  int consumed = Math.min(SurvivorProperties.MAX_WORKLOAD, r.getAmount());
		                		  r.setAmount(r.getAmount() - consumed);
		                		  response.setAmount(consumed);
		                		  if (consumed == 0)
		                			  ((World)this.myAgent).worldResources.remove(resp.getLocation());
		                			  
		                	  }else
		                		  response.setAmount(0);
		                	 
		                	  
		                	  ((World)this.myAgent).sendMessage(ACLMessage.INFORM, response, msg.getSender());		                	  
		                  }
		                     
		                  break;

		             case (ACLMessage.INFORM):
		            		
			              System.out.println("Information from " + msg.getSender().getLocalName());
			              
		             	  if (action instanceof SendAgentInfo)
		                  {
		             		  Person agentInfo = ((SendAgentInfo)action).getPerson();
		             		  
		             		  if (agentInfo.getEnergy() <= 0)
		             			 ((World)myAgent).agents.remove(msg.getSender().getLocalName());
		             		  else
		             			  ((World)myAgent).agents.put(msg.getSender().getLocalName(), agentInfo);
		                  }
		             	  else if (action instanceof SendBaseInfo)
		                  {
		             		  System.out.println("FOOOD:"+((World)myAgent).base.getFood());
		             		((World)myAgent).base.setFood( ((SendBaseInfo)action).getBaseInfo().getFood());
		             		((World)myAgent).base.setWood( ((SendBaseInfo)action).getBaseInfo().getWood());
		             		((World)myAgent).base.setStone( ((SendBaseInfo)action).getBaseInfo().getStone());
		             		  
		                  }
		             	  
		               	  break;
		                  
		            }
		         }
		         catch(Exception ex) 
		         { 
		        	 ex.printStackTrace(); 
		         }
			}
		}
	}
}

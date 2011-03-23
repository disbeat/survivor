package survivor.agents;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
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

import java.util.ArrayList;
import java.util.List;

import survivor.SurvivorProperties;
import survivor.ontology.BaseInfo;
import survivor.ontology.Food;
import survivor.ontology.Location;
import survivor.ontology.Resource;
import survivor.ontology.Stone;
import survivor.ontology.SurvivorOntology;
import survivor.ontology.Wood;
import survivor.ontology.actions.Reproduce;
import survivor.ontology.actions.RequestFood;
import survivor.ontology.actions.RequestFoodResponse;
import survivor.ontology.actions.RequestSeenResources;
import survivor.ontology.actions.RequestSeenResourcesResponse;
import survivor.ontology.actions.SendBaseInfo;
import survivor.ontology.actions.SendFoundResources;
import survivor.ontology.actions.UpdateBaseResources;

public class Base extends Agent {
	
	

	private static final long serialVersionUID = 1L;
	
	public static final int INITIAL_FOOD_VALUE = 1000;
	public static final int INITIAL_WOOD_VALUE = 1000;
	public static final int INITIAL_STONE_VALUE = 1000;
	
	private static final int INITIAL_HUNTERS = 2;
	private static final int INITIAL_SEEKERS = 5;
	private static final int INITIAL_LUMBERJACK = 1;
	private static final int INITIAL_MINER = 1;
	
	private Location myLocation;
	
	private Wood woodStock;
	private Food foodStock;
	private Stone stoneStock;
	
	private List<Resource> seenResources;
	
	private int nextHunter, nextSeeker, nextLumberjack, nextMiner;
	private int huntersCount, seekersCount, lumberjacksCount, minersCount;
	
	public Codec codec = SurvivorOntology.getCodec();
	public Ontology ontology = SurvivorOntology.getInstance();

	private AID world;
	

	public Base() {
		super();
		
		seenResources = new ArrayList<Resource>();
		woodStock = new Wood(Base.INITIAL_WOOD_VALUE);
		foodStock = new Food(Base.INITIAL_FOOD_VALUE);
		stoneStock = new Stone(Base.INITIAL_STONE_VALUE);
	}
	
	List<Resource> getResourcesLocations() {
		return this.seenResources;
	}
	
	void addResoucesLocation(Resource resource) {
		this.seenResources.add(resource);
	}
	
	void updateResourceLocation(Resource resource) {
		for(Resource r : this.seenResources)
			if (r.getLocation().equals(resource.getLocation()))
				r = resource;
				return;
	}
	
	public Food getFoodStock() {
		return foodStock;
	}
	
	public Wood getWoodStock() {
		return woodStock;
	}
	
	public Stone getStoneStock() {
		return stoneStock;
	}
	
	public void removeFood( int quantity ) {
		this.foodStock.setAmount(this.foodStock.getAmount() - quantity);
	}
	
	void createLumberjack()
	{
		jade.wrapper.AgentContainer c;
		c = getContainerController();
        try {
            jade.wrapper.AgentController a = c.createNewAgent( "Lumberjack"+this.lumberjacksCount, "survivor.agents.Lumberjack",new Object[] { this.world, this.getAID(), this.myLocation });
            a.start();
            this.lumberjacksCount++;
            this.nextLumberjack++;
        }
        catch (Exception e){e.printStackTrace();}
	}
	
	void createHunter()
	{
		jade.wrapper.AgentContainer c;
		c = getContainerController();
        try {
            jade.wrapper.AgentController a = c.createNewAgent( "Hunter"+this.nextHunter, "survivor.agents.Hunter",new Object[] { this.world, this.getAID(), this.myLocation });
            a.start();
            this.huntersCount++;
            this.nextHunter++;
        }
        catch (Exception e){e.printStackTrace();}
	}
	
	void createMiner()
	{
		jade.wrapper.AgentContainer c;
		c = getContainerController();
        try {
            jade.wrapper.AgentController a = c.createNewAgent( "Miner"+this.minersCount, "survivor.agents.Miner",new Object[] { this.world, this.getAID(), this.myLocation});
            a.start();
            this.minersCount++;
            this.nextMiner++;
        }
        catch (Exception e){e.printStackTrace();}
	}
	
	void removeResourceByLocation(Location l)
	{
		for (int i = 0; i < seenResources.size(); i++)
			if (seenResources.get(i).getLocation().equals(l))
				seenResources.remove(i);
	}
	
	void createSeeker()
	{
		jade.wrapper.AgentContainer c;
		c = getContainerController();
        try {
            jade.wrapper.AgentController a = c.createNewAgent( "Seeker"+this.seekersCount, "survivor.agents.Seeker",new Object[] { this.world, this.getAID(), this.myLocation });
            a.start();
            this.seekersCount++;
            this.nextSeeker++;
        }
        catch (Exception e){e.printStackTrace();}
	}
	
	protected void setup() {
		
		// Register language and ontology
	    getContentManager().registerLanguage(codec);
	    getContentManager().registerOntology(ontology);

	      
		System.out.println("BASE AID: "+this.getAID());
		
		this.world = (AID)getArguments()[0];
		this.myLocation = (Location)getArguments()[1];
		
		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("Base"); 
		sd.setName(getName());
		sd.setOwnership("SURVIVOR");
		
		dfd.setName(getAID());
		dfd.addServices(sd);
		
		try {
		    DFService.register(this,dfd);
		} catch (FIPAException e) {
		    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
		    doDelete();
		}
        
		int i;
		for (i=0;i<INITIAL_HUNTERS;i++)
			createHunter();
		
		for (i=0;i<INITIAL_LUMBERJACK;i++)
			createLumberjack();
		
		for (i=0;i<INITIAL_MINER;i++)
			createMiner();
		
		for (i=0;i<INITIAL_SEEKERS;i++)
			createSeeker();
		
		addBehaviour(new BaseBehaviour());
		
	}

	static class BaseBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = this.myAgent.blockingReceive();
			
			if (msg != null) 
			{
				((Base)myAgent).sendBaseInfo();
				try 
		        {
		            ContentElement content = this.myAgent.getContentManager().extractContent(msg);
		            Concept action = ((Action)content).getAction();
	
		            switch (msg.getPerformative()) {
	
		               case (ACLMessage.REQUEST):
	
		                  System.out.println("Request from " + msg.getSender().getLocalName());
	
		                  if (action instanceof RequestFood)
		                  {
		                	  RequestFood request = (RequestFood)action;
		                	  RequestFoodResponse response = new RequestFoodResponse();
		                	  if ( ((Base)this.myAgent).getFoodStock().getAmount() > request.getAmount() )
		                	  {
		                		  ((Base)this.myAgent).removeFood( request.getAmount() );
		                		  response.setAmount( request.getAmount() );
		                	  }
		                	  else
		                	  {
		                		  ((Base)this.myAgent).removeFood( request.getAmount() );
		                		  response.setAmount( request.getAmount() );
		                	  }
		                	  
		                	  ((Base)this.myAgent).sendMessage(ACLMessage.INFORM, response, msg.getSender());		                	  
	
		                  }else if (action instanceof RequestSeenResources)
		                  {
		                	  RequestSeenResources request = (RequestSeenResources)action;
		                	  RequestSeenResourcesResponse response = new RequestSeenResourcesResponse();
		                	  
		                	  jade.util.leap.ArrayList seenRes = new jade.util.leap.ArrayList();
		                	  
		                	  for (Resource resource : ((Base)myAgent).seenResources)
		                		  seenRes.add(resource);		                	  		                
		                	  
		                	  response.setResource(seenRes);
		                	  
		                	  ((Base)myAgent).sendMessage(ACLMessage.INFORM, response, msg.getSender());
		                  }
		                     
		                  break;
		               case ACLMessage.INFORM:
		            	   if (action instanceof SendFoundResources)
		            	   {
		            		   jade.util.leap.ArrayList foundResources = ((SendFoundResources)action).getResource();
		            		   for (int i = 0; i < foundResources.size(); i++)
		            		   {
		            			   ((Base)myAgent).seenResources.add((Resource)foundResources.get(i));
		            		   }
		            	   }else if (action instanceof UpdateBaseResources)
		            	   {
		            		   int amount = ((UpdateBaseResources)action).getAmount();
		            		   
		            		   if (amount == 0)
		            			   ((Base)myAgent).removeResourceByLocation(((UpdateBaseResources)action).getLocation());
		            		   
		            		   if (((UpdateBaseResources)action).getType() == SurvivorProperties.FOOD)
		            			   ((Base)myAgent).foodStock.setAmount(((Base)myAgent).foodStock.getAmount() + amount);
		            		   else if (((UpdateBaseResources)action).getType() == SurvivorProperties.WOOD)
		            			   ((Base)myAgent).woodStock.setAmount(((Base)myAgent).woodStock.getAmount() + amount);
		            		   else if (((UpdateBaseResources)action).getType() == SurvivorProperties.STONE)
		            			   ((Base)myAgent).stoneStock.setAmount(((Base)myAgent).stoneStock.getAmount() + amount);
		            		   
		            		   ((Base)myAgent).sendBaseInfo();
		            	   }else if (action instanceof Reproduce)
		            	   {
		            		   double decision = Math.random();
		            		   
		            		   if (decision > 0.65)
		            			   ((Base)myAgent).createHunter();
		            		   else if (decision > 0.3)
		            			   ((Base)myAgent).createSeeker();
		            		   else if (decision > 0.15)
		            			   ((Base)myAgent).createMiner();
		            		   else
		            			   ((Base)myAgent).createLumberjack();
		            		   
		            		   ((Base)myAgent).woodStock.setAmount(((Base)myAgent).woodStock.getAmount() - SurvivorProperties.WOOD_NEEDED);
		            		   ((Base)myAgent).stoneStock.setAmount(((Base)myAgent).stoneStock.getAmount() - SurvivorProperties.STONE_NEEDED);
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
	
	public Location getMyLocation() {
		return myLocation;
	}
	
	public void sendBaseInfo()
	{
		//TODO: TEST sendAgentInfo
		
		BaseInfo baseInfo = new BaseInfo();
		
		baseInfo.setFood(this.foodStock);
		baseInfo.setStone(this.stoneStock);
		baseInfo.setWood(this.woodStock);
		baseInfo.setLocation(myLocation);
		
		SendBaseInfo sendInfo = new SendBaseInfo();
		sendInfo.setBaseInfo(baseInfo);
		
		sendMessage(ACLMessage.INFORM, sendInfo, this.world);
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
}

package survivor.agents;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import survivor.SurvivorProperties;
import survivor.ontology.Location;
import survivor.ontology.Resource;
import survivor.ontology.SurvivorOntology;
import survivor.ontology.actions.Reproduce;
import survivor.ontology.actions.RequestFood;
import survivor.ontology.actions.RequestFoodResponse;
import survivor.ontology.actions.RequestResourceOfLocation;
import survivor.ontology.actions.RequestResourceOfLocationResponse;

public abstract class AbstractAgent extends Agent {

	private static final long serialVersionUID = 1L;
	protected AID world, base;
	protected Location currentPos, baseLocation, destination;
	protected int energy, type;
	
	protected Codec codec = SurvivorOntology.getCodec();
	protected Ontology ontology = SurvivorOntology.getInstance();
	
	public AbstractAgent() {
		super();
		getContentManager().registerLanguage(codec);
	    getContentManager().registerOntology(ontology);
	}
	
	
	protected void setup() {

	

	}
	
	protected Resource getResourceOfLocation(Location l)
	{
		RequestResourceOfLocation request = new RequestResourceOfLocation();
		request.setLocation(l);
		
		sendMessage(ACLMessage.REQUEST, request, this.world);
		
		ACLMessage reply = blockingReceive();
		if (reply != null)
		{			
			ContentElement content;
			try {
				content = getContentManager().extractContent(reply);
			
				Concept action = ((Action)content).getAction();
	            switch (reply.getPerformative()) {
	               case (ACLMessage.INFORM):	
						if (action instanceof RequestResourceOfLocationResponse)
						{
							RequestResourceOfLocationResponse response = (RequestResourceOfLocationResponse)action;
							return response.getResource();
						}
	               		break;
	            }
	            
			} catch (UngroundedException e) {
				e.printStackTrace();
			} catch (CodecException e) {
				e.printStackTrace();
			} catch (OntologyException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected boolean isAtBase() {
		if (this.currentPos.equals(this.baseLocation))
			return true;
		return false;
	}
	
	
	void eat()
	{
		RequestFood request = new RequestFood();
		request.setAmount(SurvivorProperties.MAX_ENERGY - this.energy);
		
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
						if (action instanceof RequestFoodResponse)
						{
							RequestFoodResponse response = (RequestFoodResponse)action;
							this.setEnergy( this.getEnergy() + response.getAmount() );
						}
	               		break;
	            }
	            
			} catch (UngroundedException e) {
				e.printStackTrace();
			} catch (CodecException e) {
				e.printStackTrace();
			} catch (OntologyException e) {
				e.printStackTrace();
			}
		}
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

	public int getEnergy() {
		return this.energy;
	}


	public AID getWorld() {
		return world;
	}


	public void setWorld(AID world) {
		this.world = world;
	}


	public AID getBase() {
		return base;
	}


	public void setBase(AID base) {
		this.base = base;
	}


	public Location getCurrentPos() {
		return currentPos;
	}


	public void setCurrentPos(Location currentPos) {
		this.currentPos = currentPos;
	}


	public Location getBaseLocation() {
		return baseLocation;
	}


	public void setBaseLocation(Location baseLocation) {
		this.baseLocation = baseLocation;
	}


	public Codec getCodec() {
		return codec;
	}


	public void setCodec(Codec codec) {
		this.codec = codec;
	}


	public Ontology getOntology() {
		return ontology;
	}


	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}


	public void setEnergy(int energy) {
		this.energy = energy;
	}
	
	public void setDestination(Location location) {
		this.destination = location;
	}
	
	public void move(int step)
	{
		if ( destination == null )
			return;
		
		if ( destination.getPosx() < currentPos.getPosx() )
			currentPos.setPosx( currentPos.getPosx() - step );
		else if ( destination.getPosx() > currentPos.getPosx() )
			currentPos.setPosx( currentPos.getPosx() + step );
		
		if ( destination.getPosy() < currentPos.getPosy() )
			currentPos.setPosy( currentPos.getPosy() - step );
		else if ( destination.getPosy() > currentPos.getPosy() )
			currentPos.setPosy( currentPos.getPosy() + step );
		
		System.out.println(currentPos.getPosx()+"|"+currentPos.getPosy()+" == "+destination.getPosx()+"|"+destination.getPosy());
	}
	
	public boolean isAtDestination()
	{
		return ( this.currentPos.distance( this.destination ) == 0 );
	}
	
	public void reproduce()
	{
		sendMessage(ACLMessage.INFORM, new Reproduce(), this.base);
	}
	
	
	public abstract void sendAgentInfo();
}

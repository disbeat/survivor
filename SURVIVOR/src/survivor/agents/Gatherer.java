package survivor.agents;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import survivor.ontology.Location;
import survivor.ontology.Person;
import survivor.ontology.Resource;
import survivor.ontology.actions.RequestSeenResources;
import survivor.ontology.actions.RequestSeenResourcesResponse;
import survivor.ontology.actions.SendAgentInfo;
import survivor.ontology.actions.UpdateBaseResources;

public abstract class Gatherer extends AbstractAgent {

	private static final long serialVersionUID = 1L;
	
	protected int workLoad = 0;
	
	protected Resource resourceToGather;
	
	Location lastPosition;
	
	public Gatherer() {
		super();
		getContentManager().registerLanguage(codec);
	    getContentManager().registerOntology(ontology);
	}
	
	protected void setResourceToGather(Class type, Location from, int maxDistance )
	{
		this.resourceToGather = null;
		
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
								for(int i = 0; i < resources.size(); i++)
								{
									Resource resource = (Resource) resources.get(i);
									if (resource.getClass() == type)
									{
										if (resource.getLocation().distance(from) < maxDistance)
										{
											this.resourceToGather = resource;
											break;
										}
									}
								}
							return;
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
		
		
		return;
	}
	
	public Resource getResourceToGather() {
		return resourceToGather;
	}
	
	
	public void depositResource()
	{
		//TODO: implement deposit resource
		
		UpdateBaseResources request = new UpdateBaseResources();
		request.setAmount(this.workLoad);
		request.setType(this.type);
		request.setLocation(this.lastPosition);
		sendMessage(ACLMessage.INFORM, request, this.base);
		
		this.workLoad = 0;
		
	}
	
	public abstract void gather();

	@Override
	public void sendAgentInfo()
	{
		Person agentInfo = new Person();
		
		agentInfo.setPersonLocation(this.currentPos);
		agentInfo.setEnergy(this.energy);
		agentInfo.setWorkload(this.workLoad);
		agentInfo.setType(this.type);
		
		SendAgentInfo sendInfo = new SendAgentInfo();
		sendInfo.setPerson(agentInfo);
		
		sendMessage(ACLMessage.INFORM, sendInfo, this.world);
		
		if (this.energy <= 0)
			doDelete();
	}
}

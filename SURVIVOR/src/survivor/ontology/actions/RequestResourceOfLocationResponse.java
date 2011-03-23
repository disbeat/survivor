package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.Resource;

public class RequestResourceOfLocationResponse implements AgentAction{
	
	private static final long serialVersionUID = 1L;

	Resource resource;
	
	public Resource getResource() {
		return resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

}

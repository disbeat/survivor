package survivor.ontology.actions;

import jade.content.AgentAction;
import jade.util.leap.ArrayList;

public class SendFoundResources implements AgentAction {

	private static final long serialVersionUID = 1L;

	private ArrayList resource;
	
	public ArrayList getResource() {
		return resource;
	}
	
	public void setResource(ArrayList resources) {
		this.resource = resources;
	}
}

package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.Location;

public class RequestResourceOfLocation implements AgentAction{
	
	private static final long serialVersionUID = 1L;

	Location location;
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

}

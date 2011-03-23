package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.Location;

public class RequestResourceAmountOfLocation implements AgentAction {

	Location location;
	int amount;
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	
}

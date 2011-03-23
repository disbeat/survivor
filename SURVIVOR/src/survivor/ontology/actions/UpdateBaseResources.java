package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.Location;

public class UpdateBaseResources implements AgentAction{
	
	int amount;
	int type;
	Location location;
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

}

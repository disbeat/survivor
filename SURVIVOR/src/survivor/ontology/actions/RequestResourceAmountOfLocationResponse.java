package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.Location;

public class RequestResourceAmountOfLocationResponse implements AgentAction {

	int amount;
	int type;

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
}

package survivor.ontology.actions;

import jade.content.AgentAction;

public class RequestFood implements AgentAction{
	
	private static final long serialVersionUID = 1L;

	int amount;
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}

}

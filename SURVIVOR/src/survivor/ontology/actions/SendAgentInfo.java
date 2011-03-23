package survivor.ontology.actions;

import jade.content.AgentAction;
import survivor.ontology.Person;

public class SendAgentInfo implements AgentAction {
	
	Person person;
	
	
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	

}

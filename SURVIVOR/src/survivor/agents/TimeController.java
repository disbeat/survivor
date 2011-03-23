package survivor.agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class TimeController extends Agent {
	
	

	private static final long serialVersionUID = 1L;

	public TimeController() {
		super();
	}
	
	class Tick extends TickerBehaviour {

		private static final long serialVersionUID = 1L;

		public Tick(Agent a, long period) {
			super(a, period);
		}

		protected void onTick() {
			DFAgentDescription template = new DFAgentDescription();
	  		ServiceDescription templateSd = new ServiceDescription();
	  		templateSd.setType("tick");
	  		template.addServices(templateSd);
	  		
	  		DFAgentDescription[] results;
			try {
				results = DFService.search(this.myAgent, template);
			} catch (FIPAException e) {
				e.printStackTrace();
				return;
			}
	  		ACLMessage  msg = new ACLMessage(ACLMessage.INFORM);
	  		msg.setContent("work");
	  		for (int i = 0;i<results.length;i++)
	  		{
	  			msg.addReceiver(results[i].getName());
	  		}
	  		send(msg);
		}
		
	  	
	}
	
	
	protected void setup() {
		
		System.out.println("TimeController AID: "+this.getAID());
		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("TimeController"); 
		sd.setName(getName());
		sd.setOwnership("SURVIVOR");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
		    DFService.register(this,dfd);
		} catch (FIPAException e) {
		    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
		    doDelete();
		}
		
		addBehaviour(new Tick(this, 500));
		
	}
	
}
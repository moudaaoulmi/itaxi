package itaxi.jade;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CentralServer extends Agent {

	private static final long serialVersionUID = 1L;

	private MessageTemplate _mt;

	protected void setup() {
		_mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
	}

	/**
	 * Regista o agent no DF   
	 */
	private void registerAgent() {
		// (Paginas Amarelas)
		
		DFAgentDescription df = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("CentralServer");
		sd.setName("Skynet");
		df.addServices(sd);
		
		try {
			DFService.register(this, df);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	protected void takeDown() { // Deregister from the yellow pages
		try {
			DFService.deregister(this); 
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		//myGui.dispose();
		// Printout a dismissal message
		System.out.println("CentralServer-agent " +getAID().getName()+ " terminating.");
	}
	
	class GetCallBehaviour extends OneShotBehaviour {

		private int passo = 0;

		private static final long serialVersionUID = 1L;
		
		public void action() {

			switch(passo) {
			case 0:
				passo0();
				break;
			case 1:
				//passo1();
				break;
			case 2:
				return;
				//passo2();
				//break;
			case 3:
				//passo3();
				break;
			}
		}

		private void passo0() {

			ACLMessage msg = blockingReceive(_mt);

			if(msg != null) {
				String query = msg.getContent();
				System.out.println("Costumer want to go to " + query);

				ACLMessage answer = new ACLMessage(ACLMessage.CONFIRM);

				answer.addReceiver(msg.getSender());

				// send message
				myAgent.send(answer);
			}

		}

	}
}
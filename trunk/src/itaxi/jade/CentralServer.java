package itaxi.jade;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CentralServer extends Agent {

	private static final long serialVersionUID = 1L;

	private MessageTemplate _mt;

	protected void setup() {
		_mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
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
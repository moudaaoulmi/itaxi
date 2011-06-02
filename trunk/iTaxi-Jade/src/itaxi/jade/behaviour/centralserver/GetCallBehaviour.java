package itaxi.jade.behaviour.centralserver;

import com.google.gson.Gson;

import itaxi.communications.messages.Message;
import itaxi.jade.CentralServer;
import itaxi.messages.entities.Party;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetCallBehaviour extends TickerBehaviour {

	private MessageTemplate _getCallMT;

	private CentralServer _centralServer;
	private Gson _gson;

	private static final long serialVersionUID = 1L;

	public GetCallBehaviour(Agent a, long period) {
		super(a, period);
		
		_getCallMT = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
		_centralServer = (CentralServer) a;
		_gson = new Gson();
	}


	private void passo0() {

		ACLMessage msg = myAgent.blockingReceive(_getCallMT);

		if(msg != null) {

			Message message = _gson.fromJson(msg.getContent(), Message.class);

			switch(message.getType()) {
			case PARTY:
				Party party = _gson.fromJson(message.getContent(), Party.class);
				/*System.out.println(party.getName() + " has " + party.getSize()
								+ " passengers and wants to go to " + party.getDestination());
				 */
				_centralServer.addPendingBooking(party);

				System.out.println("Pending parties:");
				for(Party p : _centralServer.getPendingBookings())
					System.out.println(p.getName() + " is at " + p.getPosition() + " and wants to go to " + p.getDestination());
				break;

			default:
				System.out.println("Message type not expected!");

			}

			ACLMessage answer = new ACLMessage(ACLMessage.CONFIRM);

			answer.addReceiver(msg.getSender());

			// send message
			myAgent.send(answer);

			myAgent.addBehaviour(new AssignTaxiBehaviour(_centralServer));

		}
	}


	@Override
	protected void onTick() {
		passo0();
	}
}
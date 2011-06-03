package itaxi.jade.behaviour.taxi;

import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.jade.Taxi;
import itaxi.jade.behaviour.centralserver.AssignTaxiBehaviour;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.PartyProposalResponse;
import itaxi.messages.entities.Vehicle;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class GetProposedPartyBehaviour extends TickerBehaviour{

	private static final long serialVersionUID = -6245822289702782240L;

	private Gson _gson;
	
	private Taxi _taxi;
	
	public GetProposedPartyBehaviour(Agent a, long period) {
		super(a, period);
		_gson = new Gson();
		_taxi = (Taxi) a;
	}

	private void step0(){
		MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
		ACLMessage msg = myAgent.blockingReceive(template);
		
		if(msg!=null){
			
			Message message = _gson.fromJson(msg.getContent(), Message.class);

			switch(message.getType()) {
				case PARTY:
					Party party = _gson.fromJson(message.getContent(), Party.class);
					Vehicle vec = _taxi.getVehicle();
					ACLMessage answer;
					
					System.out.println("Received fucking proposal!");
					
					if(vec.getPassengers() + party.getSize()<=4){
						answer = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
						_taxi.addParty(party);
						
						Message rspmsg = new Message(MessageType.PARTYPROPOSALRESPONSE);
						rspmsg.setContent(_gson.toJson(new PartyProposalResponse(_taxi.getVehicle().getVehicleID(), party.getName())));
						answer.setContent(_gson.toJson(rspmsg));
						
						System.out.println("Accept : " + party);
					}
					else{
						answer = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						System.out.println("Rejected : " + party);
					}
					
					answer.addReceiver(msg.getSender());
					// send message
					myAgent.send(answer);
					
					break;
				default:
					System.out.println("Message type not expected!");
			}
			
		}
	}
	
	@Override
	protected void onTick() {
		step0();
	}

}

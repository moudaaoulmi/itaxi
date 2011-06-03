package itaxi.jade.behaviour.taxi;

import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.jade.Taxi;
import itaxi.jade.behaviour.centralserver.AssignTaxiBehaviour;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.PartyBid;
import itaxi.messages.entities.Vehicle;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class BidForParties extends TickerBehaviour{

	private static final long serialVersionUID = -6245822289702782240L;

	private Gson _gson;
	
	private Taxi _taxi;
	
	public BidForParties(Agent a, long period) {
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
					
					double bid = vec.getPosition().distanceTo(party.getPosition());
					
					ACLMessage proposal;
					
					System.out.println(myAgent.getLocalName() + ": will bid for" + party.getName());
					
					if(vec.getPassengers() + party.getSize()<=4){
						proposal = new ACLMessage(ACLMessage.PROPOSE);
						//_taxi.addParty(party);
						
						Message msg2 = new Message(MessageType.PARTY_BID);
						msg2.setContent(_gson.toJson(new PartyBid(_taxi.getVehicle().getVehicleID(), party.getName(), bid)));
						proposal.setContent(_gson.toJson(msg2));
						
						System.out.println("bid for " + party + " with " + bid);
					}
					else{
						proposal = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
						System.out.println("Rejected : " + party);
					}
					
					proposal.addReceiver(msg.getSender());
					// send message
					myAgent.send(proposal);
					
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

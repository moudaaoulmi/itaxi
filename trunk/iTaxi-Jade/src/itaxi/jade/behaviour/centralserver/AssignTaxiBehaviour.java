package itaxi.jade.behaviour.centralserver;

import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;

import itaxi.jade.CentralServer;
import itaxi.jade.Request;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class AssignTaxiBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;

	private CentralServer _centralServer;
	private Gson _gson;
	private int _passo;
	private int _proposes;

	public AssignTaxiBehaviour(CentralServer centralServer) {
		_centralServer = centralServer;
		_gson = new Gson();
		_passo = 0;
		_proposes = 0;
	}

	@Override
	public void action() {
		switch(_passo) {
		case 0:
			propose();
			break;
		case 1:
			receiveAnswers();
			break;
		}
	}

	private void propose() {

		AID[] taxis = _centralServer.getTaxis();

		System.out.println(myAgent.getLocalName() + ": " + taxis.length + " taxi(s) available");

		if(taxis.length == 0)
			return;

		//FIXME: should only add new available taxis and not clear every time
		_centralServer.removeAllAvailableTaxis();

		for(AID taxi : taxis) {
			Vehicle vehicle = getTaxiVehicle(taxi);

			if(vehicle != null) {
				_centralServer.addAvailableTaxi(vehicle);
			}
		}

		System.out.println("Available taxis(" + _centralServer.getAvailableTaxis().size() + "):");
		for(Vehicle v : _centralServer.getAvailableTaxis())
			System.out.println("\t" + v.getVehicleID() + " at " + v.getPosition());

		for(Party p : _centralServer.getPendingBookings()) {
			Vehicle t = assignTaxiToParty(p);

			if(t != null) {
				System.out.println(p + " assigned to " + t);
				proposePartyToTaxi(p, t);
				_proposes++;
			}
		}
		
		receiveAnswers();
		_passo = 1;
	}

	private void receiveAnswers() {
		
		while(_proposes > 0) {
			
			ACLMessage msg = myAgent.blockingReceive(MessageTemplate.or(
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
					MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)));
			
			_proposes--;
			
			
			if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
				System.out.println("Proposal accepted");
				
			}
			else if(msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
				System.out.println("Proposal rejected");
			}
		}

	}

	private Vehicle getTaxiVehicle(AID taxi) {

		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

		request.addReceiver(taxi);
		request.setContent(Request.VEHICLE.toString());
		myAgent.send(request);

		ACLMessage msg = myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		if(msg != null) {
			Message message = _gson.fromJson(msg.getContent(), Message.class);
			Vehicle vehicle = _gson.fromJson(message.getContent(), Vehicle.class);
			return vehicle;
		}

		return null;
	}

	private void proposePartyToTaxi(Party party, Vehicle taxi) {

		ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);

		Message msg = new Message(MessageType.PARTY);
		msg.setContent(_gson.toJson(party));

		propose.addReceiver(new AID(taxi.getVehicleID(),false));
		propose.setContent(_gson.toJson(msg));

		// send message
		myAgent.send(propose);
	}

	private Vehicle assignTaxiToParty(Party party) {

		double minDistance = Double.MAX_VALUE;
		Vehicle assignedVehicle = null;

		for(Vehicle v : _centralServer.getAvailableTaxis()) {

			double distanceToParty = v.getPosition().distanceTo(party.getPosition());

			if(distanceToParty < minDistance) {
				minDistance = distanceToParty;
				assignedVehicle = v;
			}
		}

		if(assignedVehicle != null) {
			_centralServer.removePendingBooking(party);
			_centralServer.removeAvailableTaxi(assignedVehicle);
			return assignedVehicle;
		}
		return null;
	}
}
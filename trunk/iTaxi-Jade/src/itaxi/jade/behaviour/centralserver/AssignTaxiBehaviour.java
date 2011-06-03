package itaxi.jade.behaviour.centralserver;

import java.util.ArrayList;

import itaxi.communications.messages.Message;

import itaxi.communications.messages.MessageType;

import itaxi.jade.CentralServer;
import itaxi.jade.PartyAuction;
import itaxi.jade.Request;


import itaxi.messages.entities.Party;
import itaxi.messages.entities.PartyBid;
import itaxi.messages.entities.Vehicle;

import jade.core.AID;
import jade.core.Agent;

import jade.core.behaviours.TickerBehaviour;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class AssignTaxiBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = 1L;

	private CentralServer _centralServer;
	private Gson _gson;
	private int _passo;

	private ArrayList<PartyAuction> _activeAuctions;
	private ArrayList<PartyAuction> _finnishedAuctions;

	public AssignTaxiBehaviour(Agent a, long period) {
		super(a,period);
		_centralServer = (CentralServer) a;
		_gson = new Gson();
		_passo = 0;
		_activeAuctions = new ArrayList<PartyAuction>();
		_finnishedAuctions = new ArrayList<PartyAuction>();
	}

	private PartyAuction getActivePartyAuction(String partyName) {

		for(PartyAuction pa : _activeAuctions)
			if(pa.getPartyName().compareTo(partyName) == 0)
				return pa;

		return null;
	}

	private PartyAuction getFinnishedPartyAuction(String partyName) {

		for(PartyAuction pa : _finnishedAuctions)
			if(pa.getPartyName().compareTo(partyName) == 0)
				return pa;

		return null;
	}

	private void removeActivePartyAuction(String partyName) {

		PartyAuction remove = null;

		for(PartyAuction pa : _activeAuctions)
			if(pa.getPartyName().compareTo(partyName) == 0) {
				remove = pa;
				break;
			}

		if(remove != null)
			_activeAuctions.remove(remove);
	}

	private void removeFinnishedPartyAuction(String partyName) {

		PartyAuction remove = null;

		for(PartyAuction pa : _finnishedAuctions)
			if(pa.getPartyName().compareTo(partyName) == 0) {
				remove = pa;
				break;
			}

		if(remove != null)
			_activeAuctions.remove(remove);
	}

	@Override
	public void onTick() {
		switch(_passo) {
		case 0:
			propose();
			break;
		case 1:
			receiveBids();
			break;
		}
	}

	public ArrayList<PartyAuction> getAuctions() {
		return _activeAuctions;
	}

	public void addAuction(PartyAuction pa) {
		_activeAuctions.add(pa);
	}

	private void propose() {

		AID[] taxis = _centralServer.getTaxis();

		System.out.println(myAgent.getLocalName() + ": " + taxis.length + " taxi(s) available");

		if(taxis.length == 0)
			return;

		//FIXME: should only add new available taxis and not clear every time
		//_centralServer.removeAllAvailableTaxis();

		for(Party p : _centralServer.getPendingBookings()) {
			PartyAuction auction = new PartyAuction(p.getName(), taxis.length);
			for(AID t : taxis) {
				proposePartyToTaxi(p, t);
			}
			_activeAuctions.add(auction);
		}

		receiveBids();
	}

	private void receiveBids() {
		
		System.out.println("receiving bids");

		while(_activeAuctions.size() > 0) {
			

			//FIXME: change performative
			ACLMessage bid = myAgent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));

			Message message = _gson.fromJson(bid.getContent(), Message.class);
			PartyBid ppr = _gson.fromJson(message.getContent(), PartyBid.class);
			

			String bidPartyName = ppr.getPartyName();
			String bidTaxiName = ppr.getVehicleID();
			double bidValue = ppr.getBid();
			
			System.out.println(myAgent.getLocalName() + ": party=" + bidPartyName + " taxi=" + bidTaxiName + " bid=" + bidValue);

			PartyAuction pa = getActivePartyAuction(bidPartyName);

			pa.addBid(bidTaxiName, bidValue);

			if(pa.getRemainingBids() == 0) {
				removeActivePartyAuction(pa.getPartyName());
				_finnishedAuctions.add(pa);
			}
		}
		
		System.out.println("Will now assign parties");
		assignParties();
	}
	
	private void assignParties() {
		
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

	private void proposePartyToTaxi(Party party, AID taxi) {

		ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);

		Message msg = new Message(MessageType.PARTY);
		msg.setContent(_gson.toJson(party));

		propose.addReceiver(taxi);
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
package itaxi.jade;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class CentralServer extends Agent {

	private static final long serialVersionUID = 1L;

	private Map<String,Party> _pendingBookings = new TreeMap<String, Party>();

	protected void setup() {
		System.out.println(getLocalName() + ": initializing...");
		registerAgent();

		addBehaviour(new GetCallBehaviour());
		addBehaviour(new AssignTaxiBehaviour(this,5000));
	}

	/**
	 *  Register at the yellow pages   
	 */
	private void registerAgent() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.CENTRAL_SERVER.toString());
		sd.setName(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	/**
	 * Unregister from the yellow pages 
	 */
	protected void takeDown() { 
		try {
			DFService.deregister(this); 
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		//myGui.dispose();
		// Printout a dismissal message
		System.out.println(getLocalName() + ": terminating.");
	}

	private AID[] getTaxis() {

		DFAgentDescription dfd = new DFAgentDescription();

		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.TAXI.toString());

		dfd.addServices(sd);

		AID[] taxis = null;

		try {
			DFAgentDescription[] result = DFService.search(this, dfd);

			if(result.length == 0)
				return null;

			taxis = new AID[result.length];

			for (int i = 0; i < result.length; i++) {
				taxis[i] = result[i].getName();
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
		return taxis;
	}

	class ListTaxisBehaviour extends CyclicBehaviour {
		public void action() {
			AID[] taxis = getTaxis();

			System.out.println("Registered taxis:");
			for(AID taxi : taxis) {
				System.out.println("\t" + taxi.getName());
			}
		}
	}

	class AssignTaxiBehaviour extends TickerBehaviour {

		private static final long serialVersionUID = 1L;

		private int _passo;
		//private MessageTemplate _assignTaxiMT;
		private Gson _gson;

		public AssignTaxiBehaviour(Agent a, long period) {
			super(a, period);
			_passo = 0;
			//_assignTaxiMT = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			_gson = new Gson();
		}


		@Override
		protected void onTick() {
			System.out.println("TOU aQUI");
			switch(_passo) {
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
			
			System.out.println(getLocalName() + ": at step0 of AssignTaxiBehaviour");
			
			AID[] taxis = getTaxis();
			
			if(taxis.length == 0) {
				System.out.println(getLocalName() + ": no taxis available");
				return;
			}
			
			Map<String,Vehicle> vehicles = new TreeMap<String, Vehicle>();
			
			for(AID taxi : taxis) {
				Vehicle vehicle = getTaxiVehicle(taxi);
				
				if(vehicle != null) {
					vehicles.put(vehicle.getVehicleID(), vehicle);
				}
			}
			
			System.out.println("Available taxis:");
			for(Vehicle v : vehicles.values())
				System.out.println(v.getVehicleID() + " at " + v.getPosition());
		}
		
		private Vehicle getTaxiVehicle(AID taxi) {
			
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			
			request.addReceiver(taxi);
			
			request.setContent(Request.VEHICLE.toString());
			
			ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			
			if(msg != null) {
				Message message = _gson.fromJson(msg.getContent(), Message.class);
				
				Vehicle vehicle = _gson.fromJson(message.getContent(), Vehicle.class);

				return vehicle;
			}
			
			return null;
		}
	}

	class GetCallBehaviour extends CyclicBehaviour {

		private MessageTemplate _getCallMT = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);

		private int _passo = 0;

		private static final long serialVersionUID = 1L;

		public void action() {

			switch(_passo) {
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

			Gson gson = new Gson();
			ACLMessage msg = blockingReceive(_getCallMT);

			if(msg != null) {

				Message message = gson.fromJson(msg.getContent(), Message.class);

				switch(message.getType()) {
				case PARTY:
					Party party = gson.fromJson(message.getContent(), Party.class);
					/*System.out.println(party.getName() + " has " + party.getSize()
								+ " passengers and wants to go to " + party.getDestination());
					 */
					_pendingBookings.put(party.getName(),party);

					System.out.println("Pending parties:");
					for(Party p : _pendingBookings.values())
						System.out.println(p.getName() + " is at " + p.getPosition() + " and wants to go to " + p.getDestination());
					break;

				default:
					System.out.println("Message type not expected!");

				}

				ACLMessage answer = new ACLMessage(ACLMessage.CONFIRM);

				answer.addReceiver(msg.getSender());

				// send message
				myAgent.send(answer);
			}
		}
	}

}

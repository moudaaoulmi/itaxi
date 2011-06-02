package itaxi.jade;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.handlers.TaxiHandler;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class Taxi extends Agent {

	private static final long serialVersionUID = 1L;

	private Vehicle _vehicle;

	private String _id;

	private long[] _geoPoint = new long[2];

	//Monitor communication
	private Communicator communicator;
	private Gson _gson;
	
	public Vehicle getVehicle() {
		return _vehicle;
	}

	protected void setup() {
		System.out.println(getLocalName() + ": initializing...");

		Integer[] _destination = new Integer[2];

		_destination[0] = 39000000;
		_destination[1] = 39000000;

		Coordinates initialPosition = new Coordinates(_destination[0], _destination[1]);

		_vehicle = new Vehicle(getLocalName(), (double) 100, initialPosition);
		Object[] args = getArguments();

		registerAgent();

		//Monitor communication
		TaxiHandler handler = new TaxiHandler(getLocalName());
		//testar com tester.java a correr (escuta o porto 8000)
		communicator = new Communicator(8001, this, handler);
		communicator.start();
		
		addBehaviour(new GetRequest(this));

		// cria o comportamentos para os varios tipos de mensagem
		//addBehaviour(new ServidorInformacao());
		//addBehaviour(new ServidorRecepcaoOfertas());
		//addBehaviour(new ServidorRecepcaoPedidosCompra());
		// cria um comportamento de timeout WakerBehaviour que e' activado para terminar o leilao
		//addBehaviour(new TimeOutBehaviour(this, timeout * 1000));
	}

	/**
	 * Register at the yellow pages   
	 */
	private void registerAgent() {

		DFAgentDescription dfd = new DFAgentDescription();

		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.TAXI.toString());
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
		System.out.println(getLocalName() + ": terminating.");
	}

	private AID getCentralServer() {

		DFAgentDescription dfd = new DFAgentDescription();

		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.CENTRAL_SERVER.toString());

		dfd.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this, dfd);

			if(result.length == 0)
				return null;
			else
				return result[0].getName();

		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void handleMessage(Message message){
		//aqui trata-se da mensagem!
		System.out.println(_id + " : message handler - " + message.getContent());
		switch(message.getType()){
		}
	}


	/**
	 * Gets requests
	 */
	class GetRequest extends CyclicBehaviour {

		private Taxi _taxi; 
		private static final long serialVersionUID = 1L;

		public GetRequest(Taxi taxi) {
			_taxi = taxi;
		}

		public void action() {

			ACLMessage request = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

			if (request != null) {

				Request req = Request.valueOf(request.getContent());

				switch (req) {
				case VEHICLE:
					
					ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
					Message message = new Message(MessageType.UPDATEVEHICLE);

					message.setContent(_gson.toJson(_taxi.getVehicle()));

					inform.addReceiver(request.getSender());
					
					// send message
					myAgent.send(inform);
					break;

				default:
					break;
				}

			} else {
				block();
			}
		}
	}
}
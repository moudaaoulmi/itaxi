package itaxi.jade;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.handlers.TaxiHandler;
import itaxi.communications.messages.Message;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Vehicle;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import com.google.gson.Gson;

public class Taxi extends Agent {

	private static final long serialVersionUID = 1L;
	
	private Vehicle _vehicle;

	private String _id;

	private long[] _geoPoint = new long[2];
	
	//Monitor communication
	private Communicator communicator;
	private Gson gson;

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
		TaxiHandler handler = new TaxiHandler(_id);
		//testar com tester.java a correr (escuta o porto 8000)
		communicator = new Communicator(8001, this, handler);
		communicator.start();

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


	//FIXME:
	//	/**
	//	 * Este comportamento recebe um pedido de cliente
	//	 */
	//	class ServidorRecepcaoPedidosCompra extends CyclicBehaviour {
	//		/**
	//		 * Define as acções que o comportamento executa.
	//		 */
	//		public void action() {
	//			//Se nao ha' representante vencedor, entao e' porque o leilao nao terminou
	//			//ou ninguem fez oferta
	//			//			if(representanteVencedor == null)
	//			//				return;
	//
	//			// retira a primeira mensagem da lista de entrada
	//			ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
	//			// se a lista estiver vazia a mensagem devolvida é null
	//			if (msg != null) {
	//				// escreve o conteudo da mensagem na consola
	//				if ((msg.getSender().getLocalName().compareTo(representanteVencedor.getLocalName()) == 0)
	//						&& msg.getContent().equals("COMPRA"))
	//				{
	//					enviaResposta(msg, ACLMessage.INFORM, "");
	//					System.out.println(getLocalName() + ": Item vendido com sucesso. Terminando...");
	//					// termina a execucao
	//					doDelete();
	//				}
	//			} else {
	//				block();
	//			}
	//		}
	//	}
}
package itaxi.jade;

import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Party.PartyState;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class Customer extends Agent {

	private int _id;

	private int[] _destination = new int[2];

	private AID _centralServer;

	private MessageTemplate _callTaxiMT;

	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(getLocalName() + ": initializing...");
		Object[] args = getArguments();
		
		registerAgent();

		_destination[0] = 39000000;
		_destination[1] = 39000000;

		// time for central server to register
		try {
		Thread.sleep(500);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		_centralServer = getCentralServer();
			
		if(_centralServer == null) {
			System.out.println(getLocalName() + ": central server not found!");
			takeDown();
		}
		else
			System.out.println(getLocalName() + ": found " + _centralServer.getName());

		addBehaviour(new CallTaxiBehaviour());

	}

	/**
	 * Register at the yellow pages   
	 */
	private void registerAgent() {

		DFAgentDescription df = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Customer");
		sd.setName("Customer"+_id);
		df.addServices(sd);

		try {
			DFService.register(this, df);
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

	class CallTaxiBehaviour extends OneShotBehaviour {

		private int passo = 0;

		private static final long serialVersionUID = 1L;

		public void action() {
			while(true) {
				switch(passo) {
				case 0:
					passo0();
					break;
				case 1:
					passo1();
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
		}

		private void passo0(){

			//If there is no central server
			if(_centralServer == null){
				return;
			}

			ACLMessage queryIf = new ACLMessage(ACLMessage.QUERY_IF);
			Gson gson = new Gson();
			Message msg = new Message(MessageType.PARTY);
						
			Coordinates destination = new Coordinates(_destination[0], _destination[1]);
			Party party = new Party(0, getLocalName(), 1, destination, PartyState.BOOKED);
			
			msg.setContent(gson.toJson(party));
			
			String content = gson.toJson(msg);
			
			queryIf.addReceiver(_centralServer);
			
			// Message content is party
			queryIf.setContent(content);

			// send message
			myAgent.send(queryIf);

			System.out.println(getLocalName() + ": Sent <<" + content + ">> to " + _centralServer.getLocalName());

			// Prepare template for answer
			_callTaxiMT = MessageTemplate.or(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM));

			passo = 1;

		}

		private void passo1() {

			System.out.println(getLocalName() + ": now at step 1");

			ACLMessage msg = blockingReceive(_callTaxiMT);

			if(msg != null) {
				//String answer = msg.getContent();
				//System.out.println("Central server answered with " + answer);
				if(msg.getPerformative() == ACLMessage.CONFIRM)
					System.out.println("Central server answered with OK");
				else
					System.out.println("Central server answered with NOK");
			}

			passo = 2;
		}
	}
}

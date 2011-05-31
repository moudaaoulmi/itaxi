package itaxi.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import jade.lang.acl.MessageTemplate;

public class Customer extends Agent {

	private int _id;

	private long[] _destination = new long[2];

	private AID _centralServer;

	private MessageTemplate _mt;

	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(getLocalName() + ": A iniciar...");
		Object[] args = getArguments();

		_destination[0] = 39000000;
		_destination[1] = 39000000;

		_centralServer = getCentralServer();
		
		if(_centralServer == null) {
			//FIXME: agent can operate without central server
			return;
		}

		addBehaviour(new CallTaxiBehaviour());

		// Resgista o agente no DF
		registerAgent();

	}

	/**
	 * Regista o agent no DF   
	 */
	private void registerAgent() {
		// (Paginas Amarelas)

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

	protected void takeDown() { // Deregister from the yellow pages
		try {
			DFService.deregister(this); 
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		//myGui.dispose();
		// Printout a dismissal message
		System.out.println("CentralServer-agent " +getAID().getName()+ " terminating.");
	}

	private AID getCentralServer() {

		DFAgentDescription df = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("CentralServer");
		sd.setName("Skynet");
		df.addServices(sd);
		AID central=null;
		try {
			DFAgentDescription[] dfl = DFService.search(this, df);

			if(dfl.length == 0) {
				System.out.println(getLocalName() + ": central server not found!");
				return null;
			}
			else {
				central = dfl[0].getName();
			}


		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return central;
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

			queryIf.addReceiver(_centralServer);

			// Message content is destination
			queryIf.setContent("" + _destination[0] + _destination[1]);

			// send message
			myAgent.send(queryIf);

			System.out.println(getLocalName() + ": Sent request to " + _centralServer.getLocalName());

			// Prepare template for answer
			_mt = MessageTemplate.or(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM));

			passo = 1;

		}

		private void passo1() {

			System.out.println(getLocalName() + ": now at step 1");

			ACLMessage msg = blockingReceive(_mt);

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
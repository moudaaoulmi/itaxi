package itaxi.jade;

import itaxi.communications.messages.Message;
import itaxi.messages.entities.Party;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.google.gson.Gson;

public class CentralServer extends Agent {

	private static final long serialVersionUID = 1L;

	private MessageTemplate _mt;

	protected void setup() {
		System.out.println(getLocalName() + ": initializing...");
		registerAgent();

		_mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
		addBehaviour(new GetCallBehaviour());
		//addBehaviour(new ListTaxisBehaviour());

	}

	/**
	 * Register at the yellow pages   
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

	class GetCallBehaviour extends OneShotBehaviour {

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
			ACLMessage msg = blockingReceive(_mt);


			if(msg != null) {

				Message message = gson.fromJson(msg.getContent(), Message.class);

				switch(message.getType()) {
				case PARTY:
					Party party = gson.fromJson(message.getContent(), Party.class);
					System.out.println(party.getName() + " has " + party.getSize()
							+ " passengers and wants to go to " + party.getDestination());
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
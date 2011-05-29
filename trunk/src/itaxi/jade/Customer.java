/**
 * Leiloeiro num leilao do tipo
 * first-price sealed-bid auction
 */
package itaxi.jade;

import itaxi.jade.Taxi.ServidorRecepcaoPedidosCompra;

import java.util.Hashtable;

import java.util.Iterator;

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

	//private String _destination;

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

		addBehaviour(new CallTaxi());

		// Resgista o agente no DF
		//registaAgente();

	}

	private AID getCentralServer() {
		// TODO: use directory
		return new AID("centralServer", AID.ISLOCALNAME);
	}

	class CallTaxi extends OneShotBehaviour {

		private int passo = 0;

		private static final long serialVersionUID = 1L;

		public void action() {

			switch(passo) {
			case 0:
				passo0();
				break;
			case 1:
				//passo1();
				break;
			case 2:
				//passo2();
				break;
			case 3:
				//passo3();
				break;
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
			System.out.println(getLocalName() + ": Sent request to central server");

			// Prepare template for answer
			_mt = MessageTemplate.or(
					MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
					MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM));

			passo = 1;

		}
	}
}

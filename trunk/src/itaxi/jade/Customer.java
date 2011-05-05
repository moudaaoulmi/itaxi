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

	protected void setup() {
		System.out.println(getLocalName() + ": A iniciar...");
		Object[] args = getArguments();

		_destination[0] = 39000000;
		_destination[1] = 39000000;

		// Resgista o agente no DF
		//registaAgente();

	}

	private void passo0(){
		_centralServer = getCentralServer();

		//If there is no central server
		if(_centralServer == null){
			return;
		}

		// If there is a central server send message
		ACLMessage queryIf = new ACLMessage(ACLMessage.QUERY_IF);
		// Mensagem enviada a todos os leiloeiros
		//	for (int i = 0; i < leiloeiros.length; ++i)
		//		queryIf.addReceiver(leiloeiros[i]);
		queryIf.addReceiver(_centralServer);
		// Message content is destination
		queryIf.setContent("" + _destination[0] + _destination[1]);
		//envia a mensagem
		myAgent.send(queryIf);
		System.out.println(getLocalName() + ": Sent request to central server");

		//Reinicializa variavel para contabilizar respostas recebidas
		//		respostasRecebidas = 0;

		// Prepara o template para a resposta
		_mt = MessageTemplate.or(
				MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
				MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM));
		passo = 1;
	}

	private class RequestTaxiBehaviour extends Behaviour {
		private AID[] leiloeiros;			//Leiloeiros existentes
		private AID centralServer; 	//O leiloeiro que vende o item desejado
		private MessageTemplate mt; 		//O template para receber respostas
		private int respostasRecebidas = 0; //Contabiliza respostas recebidas

		private int passo = 0; 				//O passo do protocolo de comunicacao

		public void action() {
			switch(passo) {
			case 0:
				passo0();
				break;
			case 1:
				passo1();
				break;
			case 2:
				passo2();
				break;
			case 3:
				passo3();
				break;
			}
		}

		private AID getCentralServer() {
			// TODO: use directory
			return new AID("Servidor", AID.ISLOCALNAME);
		}
	}


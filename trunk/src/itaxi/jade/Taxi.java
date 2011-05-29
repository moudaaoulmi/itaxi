package itaxi.jade;

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

public class Taxi extends Agent {

	private int _gas;

	private int _capacity;

	private int _passengers;

	private long _geoPoint[2];

	protected void setup() {
		System.out.println(getLocalName() + ": A iniciar...");
		Object[] args = getArguments();


		// Resgista o agente no DF
		registaAgente();

		// cria o comportamentos para os varios tipos de mensagem
		//addBehaviour(new ServidorInformacao());
		//addBehaviour(new ServidorRecepcaoOfertas());
		addBehaviour(new ServidorRecepcaoPedidosCompra());
		// cria um comportamento de timeout WakerBehaviour que e' activado para terminar o leilao
		//addBehaviour(new TimeOutBehaviour(this, timeout * 1000));
	}


	/**
	 * Este comportamento recebe um pedido de cliente
	 */
	class ServidorRecepcaoPedidosCompra extends CyclicBehaviour {
		/**
		 * Define as acções que o comportamento executa.
		 */
		public void action() {
			//Se nao ha' representante vencedor, entao e' porque o leilao nao terminou
			//ou ninguem fez oferta
			//			if(representanteVencedor == null)
			//				return;

			// retira a primeira mensagem da lista de entrada
			ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			// se a lista estiver vazia a mensagem devolvida é null
			if (msg != null) {
				// escreve o conteudo da mensagem na consola
				if ((msg.getSender().getLocalName().compareTo(representanteVencedor.getLocalName()) == 0)
						&& msg.getContent().equals("COMPRA"))
				{
					enviaResposta(msg, ACLMessage.INFORM, "");
					System.out.println(getLocalName() + ": Item vendido com sucesso. Terminando...");
					// termina a execucao
					doDelete();
				}
			} else {
				block();
			}
		}
	}
}
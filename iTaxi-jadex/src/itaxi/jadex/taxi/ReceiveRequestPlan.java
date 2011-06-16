package itaxi.jadex.taxi;

import itaxi.messages.entities.Party;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

public class ReceiveRequestPlan extends Plan {

	private static final long serialVersionUID = -5319615822265712351L;

	@Override
	public void body() {
		// Message receiving in the plan.
		startAtomic();
		
		//System.out.println("Waiting for request_trip");
		//IMessageEvent request = waitForMessageEvent("request_trip");
		IMessageEvent request = (IMessageEvent)getReason();
		
		System.out.println("Received request_trip");
		Party party = (Party)request.getParameter(SFipa.CONTENT).getValue();
		
		party.set_agentID((IComponentIdentifier) request.getParameter(SFipa.SENDER).getValue());
		 
		IMessageEvent reply;
		//testes de aceitacao TODO adicionar (gasolina etc)
		if(party.getSize() > 
			(Integer)getBeliefbase().getBelief("party_capacity").getFact()) {
			System.out.println("REFUSED party!!");
			reply = getEventbase().createReply(request,"refuse_trip");
		}
		else {
			reply = getEventbase().createReply(request,"agree_trip");
			System.out.println("AGREED party!!");
			getBeliefbase().getBelief("customerAccepted").setFact(party);
		}
		
		sendMessage(reply);
		
		// Alternativa ao belief: top-level-goal q inhibits o freeroam + internal event para lancar esse goal
		// 						  o servicecustomerplan continua e no fim faz drop do top-level-goal
				
		endAtomic();
	}

}

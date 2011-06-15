package itaxi.jadex.taxi;

import itaxi.messages.entities.Party;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

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
		
		//if(party.get_destination() blabla) //TODO testes de aceitacao (gasolina etc)
		
		IMessageEvent reply = getEventbase().createReply(request,"agree_trip");
		//else IMessageEvent reply = getEventbase().createReply(request,"refuse_trip");
		sendMessage(reply);
		System.out.println("AGREED!!");
		
		// Alternativa ao belief: top-level-goal q inhibits o freeroam + internal event para lancar esse goal
		// 						  o servicecustomerplan continua e no fim faz drop do top-level-goal
		getBeliefbase().getBelief("customerAccepted").setFact(true);
				
		endAtomic();
	}

}

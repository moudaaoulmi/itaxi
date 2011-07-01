package itaxi.jadex.taxi;

import itaxi.jadex.customer.CustomerState;
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
		//testes de aceitacao
		
		final double gas = ((Double) getBeliefbase().getBelief("gas").getFact());
		final double comsumption = (Double)(getBeliefbase().getBelief("consumption").getFact());
		
		final double distance = party.get_destination().distanceTo(party.get_position());
		
		if(party.getSize() > 
			(Integer)getBeliefbase().getBelief("party_capacity").getFact()) {
			System.out.println("REFUSED party! : party capacity exceeded");
			reply = getEventbase().createReply(request,"refuse_trip");
			
		} else if ((gas-10) < (distance * comsumption)) {
			System.out.println("REFUSED party! : not enough gas for trip");
			reply = getEventbase().createReply(request,"refuse_trip");
		}
		else {
			reply = getEventbase().createReply(request,"agree_trip");
			System.out.println("AGREED party!!");
			getBeliefbase().getBelief("customerAccepted").setFact(party);
			getBeliefbase().getBelief("pickingCustomer").setFact(false);
			
			if(party.customerState() == CustomerState.IMPACIENT) {
				System.out.println("TAXI " + getScope().getAgentName() + " increased velocity to 35m/s.");
				getBeliefbase().getBelief("velocity").setFact(35);
			}
			else if(party.customerState() == CustomerState.ANGRY) {
				System.out.println("TAXI " + getScope().getAgentName() + " increased velocity to 40m/s.");
				getBeliefbase().getBelief("velocity").setFact(40);
			}

		}
		
		sendMessage(reply);
		
		// Alternativa ao belief: top-level-goal q inhibits o freeroam + internal event para lancar esse goal
		// 						  o servicecustomerplan continua e no fim faz drop do top-level-goal
				
		endAtomic();
	}

}

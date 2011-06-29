package itaxi.jadex.taxi;

import itaxi.messages.entities.Party;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;

public class PickPartyPlan extends Plan{

	private static final long serialVersionUID = 8215196765644992892L;

	public void body() {
				
		Party party = (Party) getParameter("proposal_info").getValue();
		
		System.out.println("PickPartyPlan will move to party=" + party.getPartyID() + " at " + party.get_position());
	
		getBeliefbase().getBelief("pickingCustomer").setFact(true);
		
		//contract net inform
		getParameter("result").setValue(getScope().getAgentName());
		
		IGoal moveToParty = createGoal("movetoparty");
		moveToParty.getParameter("goalLatitude").setValue(party.getPosition().getLatitude());
		moveToParty.getParameter("goalLongitude").setValue(party.getPosition().getLongitude());
		
		System.out.println(getScope().getAgentName() + " dispatched move goal to (" +
				(Integer) moveToParty.getParameter("goalLatitude").getValue() + "," +
				(Integer) moveToParty.getParameter("goalLongitude").getValue() + ")");
		
		//Top Level por causa do timeout do contract-net
		dispatchTopLevelGoal(moveToParty);
		
		//IInternalEvent event = getScope().getEventbase().createInternalEvent("pickedCustomer");
		//getScope().getEventbase().dispatchInternalEvent(event);
		
		
	}
}

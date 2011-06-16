package itaxi.jadex.taxi;

import itaxi.messages.entities.Party;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class PickPartyPlan extends Plan{

	private static final long serialVersionUID = 8215196765644992892L;

	public void body() {
		
		Party party = (Party) getParameter("proposal_info").getValue();
		
		System.out.println("PickPartyPlan will move to party=" + party.getPartyID() + " at " + party.get_position());
		
		IGoal moveToParty = createGoal("move");
		moveToParty.getParameter("goalLatitude").setValue(party.getPosition().getLatitude());
		moveToParty.getParameter("goalLongitude").setValue(party.getPosition().getLongitude());
		dispatchSubgoal(moveToParty);
		
		System.out.println(getScope().getAgentName());
		
		getParameter("result").setValue(getScope().getAgentName());
	}
}

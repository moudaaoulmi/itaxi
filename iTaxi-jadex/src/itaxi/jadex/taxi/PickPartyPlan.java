package itaxi.jadex.taxi;

import itaxi.messages.entities.Party;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class PickPartyPlan extends Plan{

	private static final long serialVersionUID = 8215196765644992892L;

	public void body() {
		
		Party party = (Party) getParameter("proposal_info").getValue();
		
		System.out.println("PickPartyPlan will move to party=" + party.getPartyID() + " at " + party.get_position());
		
		getBeliefbase().getBelief("customerAccepted").setFact(party);
		getBeliefbase().getBelief("pickingCustomer").setFact(true);
		
		IGoal moveToParty = createGoal("gotodestination");
		moveToParty.getParameter("goalLatitude").setValue(party.getPosition().getLatitude());
		moveToParty.getParameter("goalLongitude").setValue(party.getPosition().getLongitude());
		dispatchTopLevelGoal(moveToParty);
		
		System.out.println(getScope().getAgentName() + " dispatched move goal to (" +
				(Integer) moveToParty.getParameter("goalLatitude").getValue() + "," +
				(Integer) moveToParty.getParameter("goalLongitude").getValue() + ")");
		
		getParameter("result").setValue(getScope().getAgentName());
	}
}

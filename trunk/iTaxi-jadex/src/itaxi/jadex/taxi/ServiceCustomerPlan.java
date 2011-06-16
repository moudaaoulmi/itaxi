package itaxi.jadex.taxi;

import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.model.IMGoalbase;
public class ServiceCustomerPlan extends Plan {

	private static final long serialVersionUID = 4580207246903514218L;

	@Override
	public void body() {
		
		//IMGoalbase gb = (IMGoalbase)getFlyweight(getGoalbase());
		//gb.getGoal("goal");
		System.out.println("ServiceCustomerPlan!!");
		
		IGoal findCustomerGoal = createGoal("findCustomer");
		
		dispatchSubgoalAndWait(findCustomerGoal);
		
		Party party = (Party) getBeliefbase().getBelief("customerAccepted").getFact();
		
		// take customer to destination
		final Coordinates destination = party.get_destination();
		
		System.out.println("ServiceCustomerPlan: Take customer " + party.getPartyID() + " to destination:"+ destination.getLatitude());
		
		IGoal gotodestination = createGoal("gotodestination");
		gotodestination.getParameter("goalLatitude").setValue(destination.getLatitude());
		gotodestination.getParameter("goalLongitude").setValue(destination.getLongitude());

		dispatchSubgoalAndWait(gotodestination);

		// mandar mensagem ao customer a dizer q chegou ao destino
		IMessageEvent me = createMessageEvent("reached_destination");
		me.getParameterSet(SFipa.RECEIVERS).addValue(party.get_agentID());
		sendMessage(me);
		System.out.println("ServiceCustomerPlan: Reached destination.");

		getBeliefbase().getBelief("customerAccepted").setFact(null);
	}

}

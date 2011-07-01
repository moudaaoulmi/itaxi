package itaxi.jadex.taxi;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class ChargeGasPlan extends Plan {

	private static final long serialVersionUID = 172291911637341765L;

	public void body() {
		
		System.out.println("ChargeGasPlan");
		
		IGoal moveToGasStation = createGoal("gotodestination");
		moveToGasStation.getParameter("goalLatitude").setValue(getBeliefbase().getBelief("gasStationLatitude").getFact());
		moveToGasStation.getParameter("goalLongitude").setValue(getBeliefbase().getBelief("gasStationLongitude").getFact());
		
		System.out.println(getScope().getAgentName() + " dispatched move goal to Gas Station (" +
				(Integer) moveToGasStation.getParameter("goalLatitude").getValue() + "," +
				(Integer) moveToGasStation.getParameter("goalLongitude").getValue() + ")");
		
		dispatchSubgoalAndWait(moveToGasStation);
		
		System.out.println(getScope().getAgentName() + " filled tank!");

		getBeliefbase().getBelief("gas").setFact(getBeliefbase().getBelief("maxgas").getFact());
		
	}
}

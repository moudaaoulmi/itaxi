package itaxi.jadex.taxi;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.Random;

public class FreeRoamPlan extends Plan {
	private static final long serialVersionUID = -5636941172691936629L;

	// area onde o taxi pode vaguear
	// saldanha-campoPequeno-areeiro-arroios
	private final int minlat = 38733833;
	private final int maxlat = 38742034;
	private final int minlon = 9133458;
	private final int maxlon = 9145131;

	/**
	 * Create a new plan.
	 */
	public FreeRoamPlan() {
		System.out.println("FreeRoamPlan!");
	}

	/**
	 * Execute a plan.
	 */
	public void body() {
					
		/*	if((Boolean)getBeliefbase().getBelief("pickingCustomer").getFact()) {
				System.out.println("FreeRoamPlan: Waiting for pickedCustomer.");
				waitForInternalEvent("pickedCustomer");
				return;
			} */

			Random rand = new Random();
			final int destLat = rand.nextInt(maxlat - minlat + 1) + minlat;
			final int destLon = -(rand.nextInt(maxlon - minlon + 1) + minlon);

			IGoal goal = createGoal("roamArround");

			goal.getParameter("goalLatitude").setValue(destLat); // 38740662
			goal.getParameter("goalLongitude").setValue(destLon); // -9135561
																	// //TODO
																	// lat lon
			dispatchSubgoalAndWait(goal);
	}
}

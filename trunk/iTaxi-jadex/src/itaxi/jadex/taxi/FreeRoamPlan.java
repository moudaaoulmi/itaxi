package itaxi.jadex.taxi;

import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.Random;


public class FreeRoamPlan extends Plan
{
	private static final long serialVersionUID = -5636941172691936629L;

	// area onde o taxi pode vaguear
	private final int minlat = 38733833;
	private final int maxlat = 38742034;
	private final int minlon = 9133458;
	private final int maxlon = 9145131;
	
	/**
	 *  Create a new plan.
	 */
	public FreeRoamPlan()
	{
		System.out.println("FreeRoamPlan!");
	}

	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		// saldanha-campoPequeno-areeiro-arroios
		
		 
		Random rand = new Random();
		final int lat = rand.nextInt(maxlat-minlat+1)+minlat;
		final int lon = - (rand.nextInt(maxlon-minlon+1)+minlon);
		
		IGoal goal = createGoal("move");
		goal.getParameter("goalLatitude").setValue(lat);  //38740662
		goal.getParameter("goalLongitude").setValue(lon); //-9135561 //TODO lat lon

		try
		{
		  dispatchSubgoalAndWait(goal);
		  System.out.println("Dispatch move goal!");
		  //getLogger().info("Translated from "+goal+" "+
		  //word+" - "+goal.getParameter("result").getValue());
		}
		catch(GoalFailureException e)
		{
		  System.out.println("Couldn't dispatch goal move!");
		};	
	}
}

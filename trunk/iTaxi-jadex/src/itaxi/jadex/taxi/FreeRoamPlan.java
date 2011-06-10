package itaxi.jadex.taxi;

import itaxi.messages.coordinates.Coordinates;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


public class FreeRoamPlan extends Plan
{
	private static final long serialVersionUID = -5636941172691936629L;

	/**
	 *  Create a new plan.
	 */
	public FreeRoamPlan()
	{
	}

	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		Coordinates destination = new Coordinates(39001000,39001000);
		
		IGoal goal = createGoal("move");
		goal.getParameter("latitude").setValue(destination.getLatitude());
		goal.getParameter("longitude").setValue(destination.getLongitude());

		try
		{
		  dispatchSubgoal(goal);
		  //dispatchSubgoalAndWait(goal);
		  //getLogger().info("Translated from "+goal+" "+
		  //word+" - "+goal.getParameter("result").getValue());
		}
		catch(GoalFailureException e)
		{
		  System.out.println("Couldn't dispatch goal move!");
		};
		
	}
}

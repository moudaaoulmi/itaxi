package itaxi.jadex.plans.taxi;

import itaxi.messages.coordinates.Coordinates;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


public class FreeRoamPlan extends Plan
{
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
		goal.getParameter("position").setValue(destination);

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
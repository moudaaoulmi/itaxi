package itaxi.jadex.taxi;

import itaxi.messages.entities.Party;

import java.util.Random;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;


public class FreeRoamPlan extends Plan
{
	private static final long serialVersionUID = -5636941172691936629L;

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
		final int minlat = 38733833;
		final int maxlat = 38742034;
		final int minlon = 9133458;
		final int maxlon = 9145131;
		 
		Random rand = new Random();
		final int lat = rand.nextInt(maxlat-minlat+1)+minlat;
		final int lon = - (rand.nextInt(maxlon-minlon+1)+minlon);
		
		IGoal goal = createGoal("move");
		goal.getParameter("goalLatitude").setValue(38736645);  //38736645
		goal.getParameter("goalLongitude").setValue(-9138608); //-9138608 //TODO lat lon

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
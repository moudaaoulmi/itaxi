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
		
		// TODO passar isto para um plano a parte com um trigger do event + inhibits do freeroam + e mandar um internal event para parar o freeroam
		// Message receiving in the plan.
		System.out.println("Waiting for request_trip");
		IMessageEvent request = waitForMessageEvent("request_trip");
		System.out.println("Received request_trip");
		
		Party party = (Party)request.getParameter(SFipa.CONTENT).getValue();
		//if(party.get_destination() blabla) //TODO testes de aceitacao (gasolina etc)
		IMessageEvent reply = getEventbase().createReply(request,"agree_trip");
		//else IMessageEvent reply = getEventbase().createReply(request,"refuse_trip");
		sendMessage(reply);
		System.out.println("AGREED!!");
	}
}

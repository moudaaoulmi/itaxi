package itaxi.jadex.customer;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class GotoDestinationPlan extends Plan {

	private static final long serialVersionUID = 5270270661660838241L;

	/**
	 *  Create a new plan.
	 */
	public GotoDestinationPlan()
	{
		System.out.println("GotoDestinationPlan!");
	}
	
	/**
	 *  The plan body.
	 */
	public void body() {
		
		updateGUIcoordinates((Integer)getBeliefbase().getBelief("latitude").getFact(), 
							(Integer)getBeliefbase().getBelief("longitude").getFact() );
		
		
		IGoal goal = createGoal("callTaxi");
		//TODO devia mandar a party..?
		goal.getParameter("destinationLatitude").setValue(getParameter("destinationLatitude").getValue());
		goal.getParameter("destinationLongitude").setValue(getParameter("destinationLongitude").getValue());

		try
		{
		  dispatchSubgoalAndWait(goal);
		  System.out.println("Dispatch callTaxi goal!");
		  //getLogger().info("Translated from "+goal+" "+
		  //word+" - "+goal.getParameter("result").getValue());
		}
		catch(GoalFailureException e)
		{
		  System.out.println("Couldn't dispatch goal callTaxi!");
		}
			
	}
	
	private void updateGUIcoordinates(int latitude, int longitude) {

		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		String newcontent = gson.toJson(new Party(getScope().getAgentName(), 1,coords,null),Party.class);
		message.setContent(newcontent);

		Communicator.sendMessage("localhost", 8002, message);

	}
	
	
}

package itaxi.jadex.customer;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;
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
		//TODO devia mandar a party..
		goal.getParameter("latitude").setValue(getParameter("latitude"));
		goal.getParameter("longitude").setValue("longitude");

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
	
	private void updateGUIcoordinates(int latitude, int longitude) {
		// envia mensagem com as novas coordenadas para o monitor
		Communicator communicator = (Communicator) getBeliefbase().getBelief(
														"monitorCom").getFact();

		if (communicator == null) {
			communicator = new Communicator(8001, this, null);
			communicator.start();
		}

		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		String newcontent = gson.toJson(new Party(0,getScope().getAgentName(),1,coords,null));
		message.setContent(newcontent);

		communicator.sendMessage("localhost", 8002, message);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// communicator.stopThread();
	}
	
}

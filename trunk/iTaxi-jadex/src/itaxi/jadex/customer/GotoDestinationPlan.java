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
		//goal.getParameter("destinationLatitude").setValue(getParameter("destinationLatitude"));
		//goal.getParameter("destinationLongitude").setValue(getParameter("destinationLongitude"));

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
		}
			
	}
	
	private void updateGUIcoordinates(int latitude, int longitude) {
		// envia mensagem com as novas coordenadas para o monitor
		Communicator communicator = (Communicator) getBeliefbase().getBelief(
														"monitorCom").getFact();

		if (communicator == null) {
			int agentPort = 8003 + getAgentId(getScope().getAgentName());
			System.out.println("Agent Port = " + agentPort);
			communicator = new Communicator(agentPort, this, null);
			getBeliefbase().getBelief("monitorCom").setFact(communicator);
			communicator.start();
		}

		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		String newcontent = gson.toJson(new Party(0,getScope().getAgentName(),1,coords,new Coordinates(0,0)));
		message.setContent(newcontent);

		communicator.sendMessage("localhost", 8002, message);

		// communicator.stopThread();
	}
	
	private int getAgentId(String id){
		String atoi;
		if(id != null && id.length() > 0){
			for(int i=0; i < id.length(); i++)
				if(id.charAt(i) >= '0' && id.charAt(i) <= '9'){
					atoi = id.substring(i);
					return new Integer(atoi);
				}
		}
		return -1;
	}
}

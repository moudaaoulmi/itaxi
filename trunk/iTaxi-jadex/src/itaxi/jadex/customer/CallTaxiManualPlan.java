package itaxi.jadex.customer;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class CallTaxiManualPlan extends Plan {

	private static final long serialVersionUID = 5118601438419688455L;

	/**
	 *  Create a new plan.
	 */
	public CallTaxiManualPlan()
	{
		System.out.println("CallTaxiManualPlan!");
	}
	
	/**
	 *  The plan body.
	 */
	public void body() {
		
		Communicator communicator = (Communicator)getBeliefbase().getBelief("monitorCom").getFact();
		if(communicator==null) {
			communicator = new Communicator(8000,this,null);
			communicator.start();
		}
		Gson gson = new Gson();
		Message message = new Message(MessageType.PARTY_WAITING);
		String newcontent = gson.toJson(getScope().getAgentName());
		message.setContent(newcontent);
		
		communicator.sendMessage("localhost", 8002, message);
		
		
	}
	
}

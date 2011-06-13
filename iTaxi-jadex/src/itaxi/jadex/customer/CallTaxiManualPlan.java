package itaxi.jadex.customer;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import jadex.bdi.runtime.Plan;

public class CallTaxiManualPlan extends Plan {

	private static final long serialVersionUID = 5118601438419688455L;

	private final Gson gson = new Gson();

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
		
		//Communicator communicator = PlanUtil.getCommunicator(this);
		Message message = new Message(MessageType.PARTY_WAITING);
		message.setContent(getScope().getAgentName());
		
		Communicator.sendMessage("localhost", 8002, message);	
	}
		
}

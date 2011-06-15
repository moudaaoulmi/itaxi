package itaxi.jadex.customer;

import java.sql.Date;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.jadex.PlanUtil;
import itaxi.messages.entities.Party;
import itaxi.messages.exceptions.PartySizeException;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;

public class CallTaxiManualPlan extends Plan {

	private static final long serialVersionUID = 5118601438419688455L;

	private final Gson gson = new Gson();

	/**
	 * Create a new plan.
	 */
	public CallTaxiManualPlan() {
		System.out.println("CallTaxiManualPlan!");
	}

	/**
	 * The plan body.
	 */
	public void body() {
		Message message = new Message(MessageType.PARTY_WAITING);
		message.setContent(getScope().getAgentName());
		
		PlanUtil.getCommunicator(this); // ready to accept messages from the monitor
		//Date date;
		//date.getMinutes()
		Communicator.sendMessage("localhost", PlanUtil.MONITOR_PORT, message);

	}

}

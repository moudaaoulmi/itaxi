package itaxi.jadex.customer;

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
		
		Communicator.sendMessage("localhost", PlanUtil.MONITOR_PORT, message);

		IInternalEvent event = waitForInternalEvent("taxi_nearby"); // TODO TIMEOUT
		Message taximsg = (Message) event.getParameter("taxi").getValue();

		if (taximsg.getType() == MessageType.TAXI_ROAMING) {

			// Customer is near a Taxi
			IComponentIdentifier taxi = (IComponentIdentifier) gson.fromJson(taximsg.getContent(),
					ComponentIdentifier.class);

			Party party = null;
			try {
				party = new Party(getScope().getAgentName(), 1, 
						(Integer) getBeliefbase().getBelief("latitude").getFact(), 
						(Integer) getBeliefbase().getBelief("longitude").getFact(), 
						(Integer) getParameter("destinationLatitude").getValue(), 
						(Integer) getParameter("destinationLongitude").getValue());
			} catch (PartySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IMessageEvent me = createMessageEvent("request_trip");
			me.getParameterSet(SFipa.RECEIVERS).addValue(taxi);
			me.getParameter(SFipa.CONTENT).setValue(party);

			IMessageEvent reply = sendMessageAndWait(me); // TODO add timeout
			if (reply.getMessageType().equals(SFipa.AGREE)) {

			} else
				fail();
		} else
			fail();
	}

}

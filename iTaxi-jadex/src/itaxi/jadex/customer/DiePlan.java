package itaxi.jadex.customer;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import jadex.bdi.runtime.Plan;

public class DiePlan extends Plan {

	private static final long serialVersionUID = -2741448273511103962L;

	@Override
	public void body() {
		
		System.out.println("CUSTOMER DIE PLAN");
		
		Message message = new Message(MessageType.REMOVE_PARTY);
		message.setContent(getScope().getAgentName());

		Communicator.sendMessage("localhost", 8002, message);

		Communicator communicator = (Communicator) getBeliefbase().getBelief("monitorCom").getFact();
		if(communicator!=null) communicator.stopThread();
	}

}

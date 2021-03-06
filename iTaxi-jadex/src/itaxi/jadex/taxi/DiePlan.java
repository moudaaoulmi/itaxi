package itaxi.jadex.taxi;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.jadex.PlanUtil;
import jadex.bdi.runtime.Plan;

public class DiePlan extends Plan {

	private static final long serialVersionUID = -2741448273511103962L;

	@Override
	public void body() {
		
		//System.out.println("TAXI DIE PLAN");
		
		Message message = new Message(MessageType.REMOVE_VEHICLE);
		message.setContent(getScope().getAgentName());

		Communicator.sendMessage("192.168.1.84", 8002, message);		

		//Communicator communicator = (Communicator) getBeliefbase().getBelief("monitorCom").getFact();
		//if(communicator!=null) communicator.stopThread();
	}
}

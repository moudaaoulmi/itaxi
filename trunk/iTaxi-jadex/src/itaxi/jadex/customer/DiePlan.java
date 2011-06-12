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
		
		Communicator communicator = (Communicator) getBeliefbase().getBelief(
		"monitorCom").getFact();

		if (communicator == null) {
			int agentPort = 8003 + getAgentId(getScope().getAgentName());
			System.out.println("Agent Port = " + agentPort);
			communicator = new Communicator(agentPort, this, null);
			getBeliefbase().getBelief("monitorCom").setFact(communicator);
			communicator.start();
		}
		Message message = new Message(MessageType.REMOVE_PARTY);
		message.setContent(getScope().getAgentName());

		communicator.sendMessage("localhost", 8002, message);
		
		communicator.stopThread();
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

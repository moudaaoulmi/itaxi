package itaxi.jadex;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.communicator.MessageHandler;
import jadex.bdi.runtime.Plan;

public class PlanUtil {

	public static Communicator getCommunicator(Plan plan) {
		Communicator communicator = (Communicator) plan.getBeliefbase().
											getBelief("monitorCom").getFact();
		if (communicator == null) {
			communicator = new Communicator(8003+getPort(plan.getScope().getAgentName()), (MessageHandler)plan);
			plan.getBeliefbase().getBelief("monitorCom").setFact(communicator);
			communicator.start();
		}
		return communicator;
	}
	
	private static int getPort(String id){
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

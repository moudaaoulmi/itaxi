package itaxi.jadex;

import itaxi.communications.communicator.Communicator;
import itaxi.messages.coordinates.Coordinates;
import jadex.bdi.runtime.Plan;

public class PlanUtil {

	public final static int MONITOR_PORT = 8002;
	/**
	 * Gas spent rate per meter
	 */
	static double GAS_RATE = 0.00025;
	//static double GAS_RATE = 0.1;
	
	public static Communicator getCommunicator(Plan plan) {
		Communicator communicator = (Communicator) plan.getBeliefbase().
											getBelief("monitorCom").getFact();
		if (communicator == null) {
			communicator = new Communicator(getPort(plan.getScope().getAgentName()), plan.getExternalAccess());
			plan.getBeliefbase().getBelief("monitorCom").setFact(communicator);
			communicator.start();
		}
		return communicator;
	}
	
	private final static int CUSTOMER_PORTS = 55000;
	private final static int TAXI_PORTS = 57000;
	private static int getPort(String id){
		String name;
		int i_port=0;
		int nid = 0;
		if(id != null && id.length() > 0){
			for(int i=0; i < id.length(); i++)
				if(id.charAt(i) >= '0' && id.charAt(i) <= '9'){
					name = id.substring(0, i);
					if(name.equals("Customer") || name.startsWith("Customer_manual")) i_port=CUSTOMER_PORTS;
					else if(name.equals("Taxi") || name.startsWith("Taxi_manual")) i_port=TAXI_PORTS;
		
					nid = new Integer(id.substring(i));
					System.out.println("NAME:"+name + "PORT:"+ (i_port+nid));
					return i_port+nid ;
				}
		}
		return -1;
	}
	
	public static boolean inRange( int startPointLatI,  int startPointLonI,
			 int endPointLatI,  int endPointLonI,  double range) {
		return Coordinates.distanceTo(startPointLatI, startPointLonI, endPointLatI, endPointLonI)<range;
	}
	
}

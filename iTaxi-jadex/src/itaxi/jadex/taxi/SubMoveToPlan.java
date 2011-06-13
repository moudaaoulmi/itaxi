package itaxi.jadex.taxi;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Vehicle;
import jadex.bdi.runtime.Plan;
import com.google.gson.Gson;

public class SubMoveToPlan extends Plan {

	private static final long serialVersionUID = -4734256815117513268L;

	/**
	 * Create a new plan.
	 */
	public SubMoveToPlan() {
		System.out.println("SubMovePlan!");
	}

	/**
	 * The plan body.
	 */
	public void body() {
		System.out.println("SubMovePlan body!");

		final int latitude = ((Integer) getBeliefbase().getBelief("latitude")
				.getFact());
		final int longitude = ((Integer) getBeliefbase().getBelief("longitude")
				.getFact());
		
		final int step = ((Integer) getBeliefbase().getBelief("velocity").getFact());
 
		Coordinates nextCoord = Coordinates.nextCoord(latitude, longitude, 
				(Integer)getParameter("goalLatitude").getValue(), (Integer)getParameter("goalLongitude").getValue(), step);
		
		// faz set dos beliefs
		getBeliefbase().getBelief("latitude").setFact(nextCoord.getLatitude());
		getBeliefbase().getBelief("longitude").setFact(nextCoord.getLongitude());

		updateGUIcoordinates(nextCoord.getLatitude(), nextCoord.getLongitude());
	}
	
	private void updateGUIcoordinates(int latitude, int longitude) {
		
		//Communicator communicator = new Communicator(8010, this, null);
		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEVEHICLE);
		String newcontent = gson.toJson(new Vehicle(getScope().getAgentName(),
				50, coords, 0, 0, 0, ""));
		message.setContent(newcontent);
 
		Communicator.sendMessage("localhost", 8002, message);

	}

}

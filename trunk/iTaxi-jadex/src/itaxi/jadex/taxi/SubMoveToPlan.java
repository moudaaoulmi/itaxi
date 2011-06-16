package itaxi.jadex.taxi;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Vehicle;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ComponentIdentifier;

import com.google.gson.Gson;

public class SubMoveToPlan extends Plan {

	private static final long serialVersionUID = -4734256815117513268L;

	/**
	 * The plan body.
	 */
	public void body() {
		System.out.println("SubMovePlan!");

		final int latitude = ((Integer) getBeliefbase().getBelief("latitude")
				.getFact());
		final int longitude = ((Integer) getBeliefbase().getBelief("longitude")
				.getFact());

		final int goalLatitude =  (Integer) (getParameter("goalLatitude").getValue());
		final int goalLongitude =  (Integer) (getParameter("goalLongitude").getValue());
		
		final int step = ((Integer) getBeliefbase().getBelief("velocity").getFact());
 
		Coordinates nextCoord = Coordinates.nextCoord(latitude, longitude, 
				 							goalLatitude, goalLongitude, step);
		
		//System.err.println("!!!!!! latitude:"+latitude+"longitude"+longitude+" goalLatitude:"+goalLatitude + " goalLongitude:"+ goalLongitude +" step:"+step);
		//System.err.println("!!!!!! nextLatitude:"+nextCoord.getLatitude()+"nextLongitude"+nextCoord.getLongitude());
		
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
		String newcontent = gson.toJson(new Vehicle(getScope().getAgentName(),(ComponentIdentifier)(getScope().getComponentIdentifier()),100,coords),Vehicle.class);
		message.setContent(newcontent);
 
		Communicator.sendMessage("localhost", 8002, message);

	}

}

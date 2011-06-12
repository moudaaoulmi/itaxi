package itaxi.jadex.taxi;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Vehicle;
import jadex.bdi.runtime.Plan;

public class DiePlan extends Plan {

	private static final long serialVersionUID = -2741448273511103962L;

	@Override
	public void body() {
		Communicator communicator = (Communicator) getBeliefbase().getBelief(
		"monitorCom").getFact();

		if (communicator == null) {
			communicator = new Communicator(8001, this, null);
			communicator.start();
		}
		//Communicator communicator = new Communicator(8010, this, null);
		Gson gson = new Gson();
		// gera as coordenadas
		Message message = new Message(MessageType.REMOVE_VEHICLE);
		message.setContent(getScope().getAgentName());

		communicator.sendMessage("localhost", 8002, message);
		
		communicator.stopThread();
	}
}

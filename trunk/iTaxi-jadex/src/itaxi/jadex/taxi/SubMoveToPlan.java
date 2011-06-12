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
		// le os beliefs e soma 10
		int latitude = 1 + ((Integer) getBeliefbase().getBelief("latitude")
				.getFact()).intValue();
		int longitude = 1 + ((Integer) getBeliefbase().getBelief("longitude")
				.getFact()).intValue();

		// faz set dos beliefs
		getBeliefbase().getBelief("latitude").setFact(latitude);
		getBeliefbase().getBelief("longitude").setFact(longitude);

		updateGUIcoordinates(latitude, longitude);
	}

	private void updateGUIcoordinates(int latitude, int longitude) {
		// envia mensagem com as novas coordenadas para o monitor
		Communicator communicator = (Communicator) getBeliefbase().getBelief(
														"monitorCom").getFact();

		if (communicator == null) {
			communicator = new Communicator(8001, this, null);
			getBeliefbase().getBelief("monitorCom").setFact(communicator);
			communicator.start();
		}
		//Communicator communicator = new Communicator(8010, this, null);
		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEVEHICLE);
		String newcontent = gson.toJson(new Vehicle(getScope().getAgentName(),
				50, coords, 0, 0, 0, ""));
		message.setContent(newcontent);

		communicator.sendMessage("localhost", 8002, message);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// communicator.stopThread();
	}

}

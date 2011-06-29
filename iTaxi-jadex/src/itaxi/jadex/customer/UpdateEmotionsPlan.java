package itaxi.jadex.customer;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;

import java.util.Date;

import com.google.gson.Gson;

import jadex.bdi.runtime.Plan;

public class UpdateEmotionsPlan extends Plan {

	private static final long serialVersionUID = -157047142458487574L;

	public static boolean isUpdate(CustomerState state, Date date) {
		if (date == null) {
			System.out.println("date null!");
			return false;
		}

		System.out.println("date NOT null! State = " + state + " time = "
				+ ((new Date()).getTime() - date.getTime()));

		if (state == CustomerState.HAPPY) {// && (new Date()).getTime() -
											// date.getTime() > 50){
			System.out.println("date NOT null! && HAPPY && >50 ->CHANGE STATE!");
			return true;
		}

		if (state == CustomerState.IMPACIENT) {// && (new Date()).getTime() -
												// date.getTime() > 100){
			System.out.println("date NOT null! && IMPACIENT && >100 ->CHANGE STATE!");
			return true;
		}

		return true;
	}

	@Override
	public void body() {
		CustomerState state = (CustomerState) getBeliefbase().getBelief("emotional_state")
				.getFact();
		Date date = (Date) getBeliefbase().getBelief("taxi_call_time").getFact();

		if (date != null && (new Date()).getTime() - date.getTime() > 50) {

			System.out.println("Running updateEmotionsPlan!");
			switch (state) {
			case HAPPY:
				state = CustomerState.IMPACIENT;
				System.out.println("Customer state = IMPACIENT");
				break;
			case IMPACIENT:
				state = CustomerState.ANGRY;
				System.out.println("Customer state = ANGRY");
				break;
			case ANGRY:
				state = CustomerState.ANGRY;
				System.out.println("Customer state = ANGRY");
				break;
			case INIT:
				state = CustomerState.HAPPY;
				System.out.println("Customer state = HAPPY");
				break;
			}

			getBeliefbase().getBelief("emotional_state").setFact(state);

			updateGUIcoordinates((Integer) getBeliefbase().getBelief("latitude").getFact(),
					(Integer) getBeliefbase().getBelief("longitude").getFact());

		}
	}

	private void updateGUIcoordinates(int latitude, int longitude) {

		System.out.println("send UpdateParty for state!");

		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		Party p = (Party) getBeliefbase().getBelief("customerAccepted").getFact();
		CustomerState state = (CustomerState) getBeliefbase().getBelief("emotional_state")
				.getFact();
		
		if (p == null) {
			p = new Party(getScope().getAgentName(), 1, coords, null, state);

			String newcontent = gson.toJson(p, Party.class);
			message.setContent(newcontent);

			Communicator.sendMessage("localhost", 8002, message);
		}

	}

}

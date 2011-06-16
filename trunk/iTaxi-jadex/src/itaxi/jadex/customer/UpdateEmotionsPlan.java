package itaxi.jadex.customer;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;

import java.util.Date;

import com.google.gson.Gson;

import jadex.bdi.runtime.Plan;


public class UpdateEmotionsPlan extends Plan{

	private static final long serialVersionUID = -157047142458487574L;
	
	public static boolean isUpdate(CustomerState state, Date date){
		if(date==null) return false;
		
		if(state == CustomerState.HAPPY && (new Date()).getTime() - date.getTime() > 50000)
			return true;
		if(state == CustomerState.IMPACIENT && (new Date()).getTime() - date.getTime() > 100000)
			return true;
		
		return false;
	}

	@Override
	public void body() {
		CustomerState state = (CustomerState) getBeliefbase().getBelief("emotional_state").getFact();
		switch(state){
		case HAPPY:
			state = CustomerState.IMPACIENT;
			System.out.println("Customer state = IMPACIENT");
			break;
		case IMPACIENT:
			state = CustomerState.ANGRY;
			System.out.println("Customer state = ANGRY");
			break;
		case ANGRY:
			break;
		}
		
		getBeliefbase().getBelief("emotional_state").setFact(state);
		
		updateGUIcoordinates((Integer)getBeliefbase().getBelief("latitude").getFact(), 
				(Integer)getBeliefbase().getBelief("longitude").getFact() );
	}
	
	private void updateGUIcoordinates(int latitude, int longitude) {

		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		String newcontent = gson.toJson(new Party(getScope().getAgentName(), 1,coords,null,(CustomerState) getBeliefbase().getBelief("emotional_state").getFact()),Party.class);
		message.setContent(newcontent);

		Communicator.sendMessage("localhost", 8002, message);

	}

}

package itaxi.jadex.taxi;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Vehicle;
import jadex.bdi.runtime.Plan;

import com.google.gson.Gson;

public class SubMoveToPlan extends Plan
{
	
	private static final long serialVersionUID = -4734256815117513268L;
	
	/**
	 *  Create a new plan.
	 */
	public SubMoveToPlan()
	{
		System.out.println("SubMovePlan!");
	}
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		System.out.println("SubMovePlan body!");
		//le os beliefs e soma 10
		int latitude = 10 + ((Integer)getBeliefbase().getBelief("latitude").getFact()).intValue(); 
		int longitude = 10 + ((Integer)getBeliefbase().getBelief("longitude").getFact()).intValue(); 
		
		//faz set dos beliefs
		getBeliefbase().getBelief("latitude").setFact(latitude);
		getBeliefbase().getBelief("longitude").setFact(longitude);
		
		//gera as coordenadas
		Coordinates coords = new Coordinates(latitude,longitude);
		
		//envia mensagem com as novas coordenadas para o monitor
		Communicator communicator = new Communicator(8001,this,null);
		communicator.start();
		
		Gson gson = new Gson();
		
		Message message = new Message(MessageType.UPDATEVEHICLE);
		String newcontent = gson.toJson(new Vehicle("Carro do ze", 50, 
				coords, 
				0, 0, 0, ""));
		message.setContent(newcontent);
		
		
		communicator.sendMessage("localhost", 8002, message);
	}
}

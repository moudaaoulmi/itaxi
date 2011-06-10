package itaxi.jadex.plans.taxi;

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

	
	//beliefs-------------------------------------
	protected static Coordinates position;
	
	public static Coordinates getPosition()
	{
	  if(position==null)
	  {
		  position = new Coordinates(39000000,39000000);
	  }
	  return position;
	}
	
	public static void setPosition(Coordinates coords){
		position = coords;
	}
	//--------------------------------------------
	
	/**
	 *  Create a new plan.
	 */
	public SubMoveToPlan()
	{
	}
	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Coordinates coords = (Coordinates)getBeliefbase().getBelief("position");
		coords.setLatitude(coords.getLatitude()+10);
		coords.setLongitude(coords.getLongitude()+10);
		
		Communicator communicator = new Communicator(8001,this,null);
		Gson gson = new Gson();
		
		Message message = new Message(MessageType.UPDATEVEHICLE);
		String newcontent = gson.toJson(new Vehicle("Carro do ze", 50, 
				coords, 
				0, 0, 0, ""));
		message.setContent(newcontent);
		
		
		communicator.sendMessage("localhost", 8002, message);
	}
}

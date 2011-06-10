package itaxi.jadex.plans.taxi;

import itaxi.messages.coordinates.Coordinates;

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

	}
}

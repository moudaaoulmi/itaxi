package itaxi.jadex.customer;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import itaxi.messages.exceptions.PartySizeException;
import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;

public class GotoDestinationPlan extends Plan {

	private static final long serialVersionUID = 5270270661660838241L;

	/**
	 *  Create a new plan.
	 */
	public GotoDestinationPlan()
	{
		System.out.println("GotoDestinationPlan!");
	}
	
	/**
	 *  The plan body.
	 */
	public void body() {
		
		updateGUIcoordinates((Integer)getBeliefbase().getBelief("latitude").getFact(), 
							(Integer)getBeliefbase().getBelief("longitude").getFact() );
		
		
		IGoal goal = createGoal("callTaxi");
		//TODO devia mandar a party..?
		goal.getParameter("destinationLatitude").setValue(getParameter("destinationLatitude").getValue());
		goal.getParameter("destinationLongitude").setValue(getParameter("destinationLongitude").getValue());

		try
		{
		  dispatchSubgoalAndWait(goal);
		  System.out.println("Dispatched callTaxi goal!");
		  //getLogger().info("Translated from "+goal+" "+
		  //word+" - "+goal.getParameter("result").getValue());
		}
		catch(GoalFailureException e)
		{
		  System.out.println("Couldn't dispatch goal callTaxi!");
		}
		
		
		// ---------    EnterTaxiPlan
		IInternalEvent event = waitForInternalEvent("taxi_nearby"); // TODO TIMEOUT
		Message taximsg = (Message) event.getParameter("taxi").getValue();

		if (taximsg.getType() == MessageType.TAXI_ROAMING) {

			// Customer is near a Taxi
			IComponentIdentifier taxi = (IComponentIdentifier) new Gson().fromJson(taximsg.getContent(),
					ComponentIdentifier.class);

			Party party = null;
			try {
				party = new Party(getScope().getAgentName(), 1, 
						(Integer) getBeliefbase().getBelief("latitude").getFact(), 
						(Integer) getBeliefbase().getBelief("longitude").getFact(), 
						(Integer) getParameter("destinationLatitude").getValue(), 
						(Integer) getParameter("destinationLongitude").getValue());
			} catch (PartySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IMessageEvent me = createMessageEvent("request_trip");
			me.getParameterSet(SFipa.RECEIVERS).addValue(taxi);
			me.getParameter(SFipa.CONTENT).setValue(party);

			IMessageEvent reply = sendMessageAndWait(me); // TODO add timeout
			if (reply.getMessageType().equals(SFipa.AGREE)) {

			} else
				fail();
		} else
			fail();
			
	}
	
	private void updateGUIcoordinates(int latitude, int longitude) {

		Gson gson = new Gson();
		// gera as coordenadas
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		String newcontent = gson.toJson(new Party(getScope().getAgentName(), 1,coords,null),Party.class);
		message.setContent(newcontent);

		Communicator.sendMessage("localhost", 8002, message);

	}
	
	
}

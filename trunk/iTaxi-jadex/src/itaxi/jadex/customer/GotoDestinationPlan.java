package itaxi.jadex.customer;

import com.google.gson.Gson;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
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
	private Gson gson = new Gson();
	
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
		
		final int latitude = ((Integer) getBeliefbase().getBelief("latitude").getFact());
		final int longitude = ((Integer) getBeliefbase().getBelief("longitude").getFact());

		final int destinationLatitude =  (Integer) (getParameter("destinationLatitude").getValue());
		final int destinationLongitude =  (Integer) (getParameter("destinationLongitude").getValue());
		
		
		updateGUIcoordinates(latitude, longitude);
		
		
		IGoal goal = createGoal("callTaxi");
		//TODO devia mandar a party..?
		goal.getParameter("destinationLatitude").setValue(destinationLatitude);
		goal.getParameter("destinationLongitude").setValue(destinationLongitude);

		try
		{
		  dispatchSubgoalAndWait(goal);
		  System.out.println("Dispatched callTaxi goal!");
		}
		catch(GoalFailureException e)
		{
		  System.out.println("Couldn't dispatch goal callTaxi!");
		}
		
		
		// ---------    EnterTaxiPlan ----------
		IInternalEvent event = waitForInternalEvent("taxi_nearby"); // TODO TIMEOUT
		Message taximsg = (Message) event.getParameter("taxi").getValue();

		if (taximsg.getType() == MessageType.TAXI_ROAMING) {

			// Customer is near a Taxi
			IComponentIdentifier taxi = (IComponentIdentifier) new Gson().fromJson(
												taximsg.getContent(), ComponentIdentifier.class);

			Party party = null;
			
			party = new Party(getScope().getAgentName(), 1, 
								latitude, 
								longitude, 
								destinationLatitude, 
								destinationLongitude);

			IMessageEvent me = createMessageEvent("request_trip");
			me.getParameterSet(SFipa.RECEIVERS).addValue(taxi);
			me.getParameter(SFipa.CONTENT).setValue(party);

			IMessageEvent reply = sendMessageAndWait(me); // TODO add timeout
			if (reply.getParameter(SFipa.PERFORMATIVE).getValue().equals(SFipa.AGREE)) {
				System.out.println("Customer: Taxi agreed trip.");
				
				// entra no veiculo por isso desaparece do mapa
				disappearGUI();
				
				// espera por mensagem do taxi a dizer q chegou ao destino
				waitForMessageEvent("reached_destination"); //TODO timeout?
				getBeliefbase().getBelief("latitude").setFact(destinationLatitude);
				getBeliefbase().getBelief("longitude").setFact(destinationLongitude);
				updateGUIcoordinates(destinationLatitude, destinationLongitude);
				getBeliefbase().getBelief("taxi_call_time").setFact(null);
				System.out.println("Customer: Reached destination!!");
				
			} else {
				System.out.println("Customer: Taxi refused trip:");
				fail();
			}	
		} else
			fail();
	}
	
	private void updateGUIcoordinates(int latitude, int longitude) {
		Coordinates coords = new Coordinates(latitude, longitude);
		Message message = new Message(MessageType.UPDATEPARTY);
		String newcontent = gson.toJson(new Party(getScope().getAgentName(), 1,coords,null,(CustomerState) getBeliefbase().getBelief("emotional_state").getFact()),Party.class);
		message.setContent(newcontent);

		Communicator.sendMessage("localhost", 8002, message);

	}
	
	private void disappearGUI() {
		Message message = new Message(MessageType.REMOVE_PARTY);
		message.setContent(getScope().getAgentName());
		Communicator.sendMessage("localhost", 8002, message);
	}
	
	
	
	
}

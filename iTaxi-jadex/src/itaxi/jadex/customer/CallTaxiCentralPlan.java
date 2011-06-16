package itaxi.jadex.customer;

import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import jadex.base.fipa.SFipa;
import jadex.base.service.awareness.AwarenessAgentPanel.GetDelayCommand;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

public class CallTaxiCentralPlan extends Plan {

	private static final long serialVersionUID = -4876028484315191150L;

	public void body() {
		
		System.out.println("CallTaxiCentralPlan");
		
		int positionLatitude = (Integer) getBeliefbase().getBelief("latitude").getFact();
		int positionLongitude = (Integer) getBeliefbase().getBelief("longitude").getFact();
		
		int destinationLatitude = (Integer) getParameter("destinationLatitude").getValue();
		int destinationLongitude = (Integer) getParameter("destinationLongitude").getValue();
		
		Coordinates position = new Coordinates(positionLatitude,positionLongitude);
		Coordinates destination = new Coordinates(destinationLatitude,destinationLongitude);
		
		Party party = new Party(getScope().getAgentName(), 1,position,destination,null);
		
		// get centralServer
		IGoal getCentralServerGoal = createGoal("getCentralServerGoal");
		dispatchSubgoalAndWait(getCentralServerGoal);
		IComponentIdentifier[] result = (IComponentIdentifier[]) getCentralServerGoal.getParameter("centralServer").getValue();
		
		System.out.println("finnished finding centralServer");
		
		if(result == null) {
			System.out.println("no taxis centralServer");
			return;
		}
		
		IComponentIdentifier centralServer = result[0];
		
		System.out.println("will send request to " + centralServer.getLocalName());
		
		IMessageEvent me = createMessageEvent("requestTaxi");
		me.getParameterSet(SFipa.RECEIVERS).addValue(centralServer);
		me.getParameter(SFipa.CONTENT).setValue(party);
		
		sendMessage(me);
		
		System.out.println("message sent");
	}
}

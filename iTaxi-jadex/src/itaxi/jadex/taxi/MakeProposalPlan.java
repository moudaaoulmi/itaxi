package itaxi.jadex.taxi;

import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import jadex.bdi.runtime.Plan;

public class MakeProposalPlan extends Plan {

	public void body() {
		// TODO Auto-generated method stub
		
		int latidude = (Integer) getBeliefbase().getBelief("latidude").getFact();
		int longitude = (Integer) getBeliefbase().getBelief("longitude").getFact();
		Coordinates taxiPosition = new Coordinates(latidude,longitude);
		
		
		Party party = (Party) getParameter("cfp").getValue();
		Coordinates partyDestination = party.getPosition();
		
		double distance = taxiPosition.distanceTo(partyDestination);
				
		
		getParameter("proposal").setValue(new Double(distance));
		//getParameter("proposal_info").setValue(order);
		
	}

	
}

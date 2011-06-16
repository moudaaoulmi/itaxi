package itaxi.jadex.taxi;

import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Party;
import jadex.bdi.runtime.Plan;

public class MakeProposalPlan extends Plan {

	private static final long serialVersionUID = -7351446683085086978L;

	public void body() {

		int latidude = (Integer) getBeliefbase().getBelief("latitude").getFact();
		int longitude = (Integer) getBeliefbase().getBelief("longitude").getFact();
		Coordinates taxiPosition = new Coordinates(latidude,longitude);


		Party party = (Party) getParameter("cfp").getValue();
		Coordinates partyDestination = party.getPosition();

double distance;

		if(getBeliefbase().getBelief("customerAccepted").getFact() == null &&
				!(Boolean)getBeliefbase().getBelief("pickingCustomer").getFact())
			distance = taxiPosition.distanceTo(partyDestination);
		else
			distance = Double.MAX_VALUE;

		System.out.println("bid=" + distance);

		getParameter("proposal").setValue(new Double(distance));
		getParameter("proposal_info").setValue(party);
	}


}

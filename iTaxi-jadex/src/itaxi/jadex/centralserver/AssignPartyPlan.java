package itaxi.jadex.centralserver;

import itaxi.messages.entities.Party;
import itaxi.messages.entities.PartyBid;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

public class AssignPartyPlan extends Plan {
	
	private static final long serialVersionUID = -8979193039649876573L;

	public void body() {
		
		// find available taxi agents
		IGoal getTaxis = createGoal("getTaxis");
		dispatchSubgoalAndWait(getTaxis);
		IComponentIdentifier[] taxis = (IComponentIdentifier[]) getTaxis.getParameter("taxis").getValue();
		
		
		Party party = (Party) getParameter("party").getValue();
		
		// Initiate a call-for-proposal.
		IGoal cnp = createGoal("cnp_initiate");
		cnp.getParameter("cfp").setValue(party);
		//cnp.getParameter("cfp_info").setValue(new Integer(acceptable_price));
		cnp.getParameterSet("receivers").addValues(taxis);
		
		dispatchSubgoalAndWait(cnp);
		
		PartyBid winningBid = (PartyBid) cnp.getParameter("result");
		
		System.out.println("winingBidder: " + winningBid.getVehicleID() + 
				" bid: " + winningBid.getBid());
	}
}

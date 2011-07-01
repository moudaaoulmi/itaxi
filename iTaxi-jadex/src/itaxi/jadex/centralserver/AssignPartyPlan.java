package itaxi.jadex.centralserver;

import itaxi.messages.entities.Party;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

public class AssignPartyPlan extends Plan {
	
	private static final long serialVersionUID = -8979193039649876573L;

	public void body() {
		
		System.out.println("AssignPartyPlan");
		
		// find available taxi agents
		IGoal getTaxisGoal = createGoal("getTaxisGoal");
		dispatchSubgoalAndWait(getTaxisGoal);
		IComponentIdentifier[] taxis = (IComponentIdentifier[]) getTaxisGoal.getParameter("taxis").getValue();
		
		System.out.println("finnished finding taxis");
		
		if(taxis == null) {
			System.out.println("no taxis available");
			return;
		}	
		
		Party party = (Party) getParameter("party").getValue();
		//if(party == null)
			//System.out.println("ta null");
		//else
			//System.out.println("nao ta null");
		
		System.out.println("Assign Party name=" + party.getPartyID());
		
		// Initiate a call-for-proposal.
		IGoal cnp = createGoal("cnp_initiate");
		cnp.getParameter("cfp").setValue(party);
		//cnp.getParameter("cfp_info").setValue(new Integer(acceptable_price));
		cnp.getParameterSet("receivers").addValues(taxis);
		
		try {
			dispatchSubgoalAndWait(cnp);
		} catch(GoalFailureException e) {
			System.out.println("No available taxis.");
			//throw e;
		}
		
		String winningBidder = (String) cnp.getParameterSet("result").getValues()[0];
		
		System.out.println("winingBidder= " + winningBidder);
	}
}

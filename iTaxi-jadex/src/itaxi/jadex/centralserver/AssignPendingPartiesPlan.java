package itaxi.jadex.centralserver;

import itaxi.messages.entities.Party;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;

import java.util.ArrayList;

public class AssignPendingPartiesPlan extends Plan{

	private static final long serialVersionUID = 4694618395531571410L;

	@Override
	public void body() {
		System.out.println("#################Superb AssignPendingPartiesPlan!");

		ArrayList<Party> pendingParties = (ArrayList<Party>) getBeliefbase().getBelief("pendingParties").getFact();
		ArrayList<Party> resultParties;
		
		if(pendingParties.size() == 0){
			System.out.println("No pending parties!");
			return;
		}
		
		resultParties = new ArrayList<Party>(pendingParties);
		
		// find available taxi agents
		IGoal getTaxisGoal = createGoal("getTaxisGoal");
		dispatchSubgoalAndWait(getTaxisGoal);
		IComponentIdentifier[] taxis = (IComponentIdentifier[]) getTaxisGoal
		.getParameter("taxis").getValue();

		System.out.println("finnished finding taxis");

		if (taxis == null) {
			System.out.println("no taxis available");
			return;
		}

		for(Party party : pendingParties){

			System.out.println("Party name=" + party.getPartyID());

			// Initiate a call-for-proposal.
			IGoal cnp = createGoal("cnp_initiate");
			cnp.getParameter("cfp").setValue(party);
			// cnp.getParameter("cfp_info").setValue(new Integer(acceptable_price));
			cnp.getParameterSet("receivers").addValues(taxis);

			try {
				dispatchSubgoalAndWait(cnp);
			} catch (GoalFailureException e) {
				System.out.println("No available taxis.");
				// throw e;
			}

			IParameterSet result = cnp.getParameterSet("result");

			if(result.getValues().length != 0) {
				System.out.println("No available taxis add party to list");
				resultParties.remove(party);
				
				String winningBidder = (String) cnp.getParameterSet("result")
				.getValues()[0];

				System.out.println("winingBidder= " + winningBidder);
			}
		}
		
		getBeliefbase().getBelief("pendingParties").setFact(resultParties);

	}

}

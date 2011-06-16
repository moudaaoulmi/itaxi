package itaxi.jadex.centralserver;

import jadex.bdi.planlib.protocols.ParticipantProposal;
import jadex.bdi.runtime.Plan;

public class EvaluateProposalsPlan extends Plan {

	private static final long serialVersionUID = -8211856963175907249L;

	public void body() {

		double minDistance = Double.MAX_VALUE;
		ParticipantProposal bestProposal = null;

		ParticipantProposal[] proposals = (ParticipantProposal[]) getParameterSet("proposals").getValues();

		for(ParticipantProposal pp : proposals) {
			Double bid = (Double) pp.getProposal(); 
			if(bid < minDistance) {
				minDistance = bid;
				bestProposal = pp;
			}
		}
		
		System.out.println("best bid=" + (Double) bestProposal.getProposal());
		
		getParameterSet("acceptables").addValue(bestProposal);
	}
}

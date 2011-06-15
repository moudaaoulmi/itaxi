package itaxi.jadex.centralserver;

import itaxi.messages.entities.PartyBid;
import jadex.bdi.runtime.Plan;

public class EvaluateProposalsPlan extends Plan {

	private static final long serialVersionUID = -8211856963175907249L;

	public void body() {

		double minDistance = Double.MAX_VALUE;
		PartyBid bestBid = null;

		PartyBid[] proposals = (PartyBid[])getParameterSet("proposals").getValues();

		for(PartyBid pb : proposals)
			if(pb.getBid() < minDistance) {
				minDistance = pb.getBid();
				bestBid = pb;
			}
		
		getParameterSet("acceptables").addValue(bestBid);
	}
}

package itaxi.jade;

import java.util.TreeMap;

import jade.core.AID;
import java.util.Map;

public class PartyAuction {
	
	private String _partyName;
	private int _remainingBids;
	private Map<AID, Double> _bids;
	
	public PartyAuction(String partyName, int remainingBids) {
		_partyName = partyName;
		_remainingBids = remainingBids;
		_bids = new TreeMap<AID, Double>();
	}
	
	public void addBid(String bidder, double bid) {
		_bids.put(new AID(bidder,false), bid);
		_remainingBids--;
	}

	public String getPartyName() {
		return _partyName;
	}

	public int getRemainingBids() {
		return _remainingBids;
	}
}

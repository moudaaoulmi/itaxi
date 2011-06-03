package itaxi.messages.entities;

public class PartyBid {
	
	private String _vehicleID;
	private String _partyName;
	private double _bid;
	
	public PartyBid(){}
	
	public PartyBid(String vehicleID, String partyID, double bid) {
		_vehicleID = vehicleID;
		_partyName = partyID;
		_bid = bid;
	}

	public void setVehicleID(String _vehicleID) {
		this._vehicleID = _vehicleID;
	}

	public String getVehicleID() {
		return _vehicleID;
	}

	public void setPartyID(String _partyID) {
		this._partyName = _partyID;
	}

	public String getPartyName() {
		return _partyName;
	}
	
	public double getBid() {
		return _bid;
	}
	
	
}

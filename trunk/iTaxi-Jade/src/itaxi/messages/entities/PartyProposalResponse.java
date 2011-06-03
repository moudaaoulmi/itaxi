package itaxi.messages.entities;

public class PartyProposalResponse {
	private String _vehicleID;
	private String _partyID;
	
	public PartyProposalResponse(){}
	
	public PartyProposalResponse(String vehicleID, String partyID){
		_vehicleID = vehicleID;
		_partyID = partyID;
	}

	public void setVehicleID(String _vehicleID) {
		this._vehicleID = _vehicleID;
	}

	public String getVehicleID() {
		return _vehicleID;
	}

	public void setPartyID(String _partyID) {
		this._partyID = _partyID;
	}

	public String getPartyID() {
		return _partyID;
	}
	
	
}

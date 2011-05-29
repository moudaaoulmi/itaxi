package itaxi.messages.entities;

import java.util.Map;
import java.util.TreeMap;

public class Ship {

	private int vehicle;
	private Map<String, Party> parties;
	
	public Ship() {
		
	}
	
	public Ship(int vehicle) {
		this.vehicle = vehicle;
		this.parties = new TreeMap<String, Party>();
	}
	
	public int getVehicle() {
		return vehicle;
	}
	
	public void setVehicle(int vehicle) {
		this.vehicle = vehicle;
	}

	public void setParties(Map<String, Party> parties) {
		this.parties = parties;
	}

	public Map<String, Party> getParties() {
		return parties;
	}
	
	public void addParty(Party party) {
		this.parties.put(party.getName(), party);
	}

}

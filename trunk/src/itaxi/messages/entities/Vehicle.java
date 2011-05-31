package itaxi.messages.entities;

import itaxi.messages.coordinates.Coordinates;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Vehicle {

	private double _gasLevel;
	
	private String _vehicleID;
	
	private Coordinates _initialPosition;
	
	private int parked;
	
	private long infoAge;
	
	private String otherVehicles;
	
	private Map<Integer, Vehicle> interactions;
	
	private Map<String, Party> parties;
	
	private LinkedList<Coordinates> destinations;
	
	private String lastknownpath;

	public Vehicle(){
	}
	
	public Vehicle(String vehicleID, double gasLevel, Coordinates initialPosition, int altitude,
			int parked, long infoAge, String otherVehicles) {
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_initialPosition = initialPosition;
		this.infoAge = infoAge;
		this.interactions = new TreeMap<Integer, Vehicle>();
		this.otherVehicles = otherVehicles;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
		this.lastknownpath = "";
	}
	
	public Vehicle(String vehicleID, double gasLevel, Coordinates initialPosition) {
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_initialPosition = initialPosition;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
	}


	public String getLastknownpath() {
		return lastknownpath;
	}

	public void setLastknownpath(String lastknownpath) {
		this.lastknownpath = lastknownpath;
	}

	public int getParked() {
		return parked;
	}

	public void setParked(int parked) {
		this.parked = parked;
	}

	public double getBatteryLevel() {
		return _gasLevel;
	}

	public void setBatteryLevel(double battery) {
		this._gasLevel = battery;
	}

	public String getVehicleID() {
		return _vehicleID;
	}

	public void setVehicleID(String vehicleID) {
		_vehicleID = vehicleID;
	}

	public Coordinates getPosition() {
		return _initialPosition;
	}

	public void setPosition(Coordinates position) {
		this._initialPosition = position;
	}
		
	public Map<String, Party> getParties() {
		return parties;
	}

	public void setParties(Map<String, Party> parties) {
		this.parties = parties;
	}
	
	public void addParty(Party party) {
		this.parties.put(party.getName(), party);
	}
	
	public void removeParty(String partyName) {
		this.parties.remove(partyName);
	}
	
	public LinkedList<Coordinates> getDestinations() {
		return destinations;
	}

	public void setDestinations(LinkedList<Coordinates> destinations) {
		this.destinations = destinations;
	}
	
	public int getPassengers() {
		int number = 0;
		for(Party party : parties.values()) {
			number += party.getSize();
		}
		return number;
	}
	
	public long getInfoAge() {
		return infoAge;
	}

	public void setInfoAge(long infoAge) {
		this.infoAge = infoAge;
	}
	
	public Map<Integer, Vehicle> getInteractions() {
		return interactions;
	}

	public void setInteractions(Map<Integer, Vehicle> interactions) {
		this.interactions = interactions;
	}
	
	public String getOtherVehicles() {
		return otherVehicles;
	}

	public void setOtherVehicles(String otherVehicles) {
		this.otherVehicles = otherVehicles;
	}
}

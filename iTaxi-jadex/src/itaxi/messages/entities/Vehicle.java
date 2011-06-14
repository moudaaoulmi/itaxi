package itaxi.messages.entities;

import itaxi.messages.coordinates.Coordinates;

import jadex.bridge.ComponentIdentifier;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Vehicle {
	
	private ComponentIdentifier _agentID;
	
	private double _gasLevel;
	
	private String _vehicleID;
	
	private Coordinates _position;
	
	private int parked;
	
	private long infoAge;
	
	private String otherVehicles;
	
	private Map<Integer, Vehicle> interactions;
	
	private Map<String, Party> parties;
	
	private LinkedList<Coordinates> destinations;
	
	private String lastknownpath;

	public Vehicle(){
	}
	
	public Vehicle(String vehicleID,  ComponentIdentifier agentID, double gasLevel, Coordinates initialPosition, int altitude,
			int parked, long infoAge, String otherVehicles) {
		_agentID=(ComponentIdentifier)agentID;
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_position = initialPosition;
		this.infoAge = infoAge;
		this.interactions = new TreeMap<Integer, Vehicle>();
		this.otherVehicles = otherVehicles;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
		this.lastknownpath = "";
	}
	
	public Vehicle(String vehicleID, double gasLevel, Coordinates initialPosition, int altitude,
			int parked, long infoAge, String otherVehicles) {
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_position = initialPosition;
		this.infoAge = infoAge;
		this.interactions = new TreeMap<Integer, Vehicle>();
		this.otherVehicles = otherVehicles;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
		this.lastknownpath = "";
	}
	
	public Vehicle(String vehicleID, ComponentIdentifier agentID, double gasLevel, Coordinates initialPosition) {
		_vehicleID = vehicleID;
		_agentID=agentID;
		_gasLevel = gasLevel;
		_position = initialPosition;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
	}
	
	public Vehicle(String vehicleID, double gasLevel, Coordinates initialPosition) {
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_position = initialPosition;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
	}
	
	public String toString() {
		return "[VEHICLE] name=" + _vehicleID + " gas=" + _gasLevel + " position=" + _position;
	}

	public double get_gasLevel() {
		return _gasLevel;
	}

	public void set_gasLevel(double _gasLevel) {
		this._gasLevel = _gasLevel;
	}

	public String get_vehicleID() {
		return _vehicleID;
	}

	public void set_vehicleID(String _vehicleID) {
		this._vehicleID = _vehicleID;
	}

	public Coordinates get_position() {
		return _position;
	}

	public void set_position(Coordinates _position) {
		this._position = _position;
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
		return _position;
	}

	public void setPosition(Coordinates position) {
		this._position = position;
	}
		
	public Map<String, Party> getParties() {
		return parties;
	}

	public void setParties(Map<String, Party> parties) {
		this.parties = parties;
	}
	
	public void addParty(Party party) {
		this.parties.put(party.getPartyID(), party);
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

	public void set_agentID(ComponentIdentifier _agentID) {
		this._agentID = (ComponentIdentifier)_agentID;
	}

	public ComponentIdentifier get_agentID() {
		return _agentID;
	}
}

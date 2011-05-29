package itaxi.messages.entities;

import itaxi.messages.coordinates.Coordinates;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Vehicle {

	public enum BatteryStatus {CHARGING,DISCHARGING};
	private double batteryLevel;
	private BatteryStatus status;
	private int vehicleID;
	private Coordinates position;
	private int altitude;
	private int parked;
	private long infoAge;
	private String otherVehicles;
	private Map<Integer, Vehicle> interactions;
	private Map<String, Party> parties;
	private LinkedList<Coordinates> destinations;
	private String lastknownpath;

	public Vehicle(){
	}
	
	public Vehicle(int vehicleID, double battery, BatteryStatus status, Coordinates position, int altitude,
			int parked, long infoAge, String otherVehicles) {
		this.batteryLevel = battery;
		this.vehicleID = vehicleID;
		this.position = position;
		this.altitude = altitude;
		this.status = status;
		this.parked = parked;
		this.infoAge = infoAge;
		this.interactions = new TreeMap<Integer, Vehicle>();
		this.otherVehicles = otherVehicles;
		this.parties = new TreeMap<String, Party>();
		this.destinations = new LinkedList<Coordinates>();
		this.lastknownpath = "";
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
		return batteryLevel;
	}

	public void setBatteryLevel(double battery) {
		this.batteryLevel = battery;
	}

	public int getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(int vehicleID) {
		this.vehicleID = vehicleID;
	}

	public Coordinates getPosition() {
		return position;
	}

	public void setPosition(Coordinates position) {
		this.position = position;
	}

	public BatteryStatus getStatus() {
		return status;
	}

	public void setStatus(BatteryStatus status) {
		this.status = status;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
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

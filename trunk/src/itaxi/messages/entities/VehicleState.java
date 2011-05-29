package itaxi.messages.entities;

import java.util.LinkedList;

import smartfleet.coordinates.Coordinates;

public class VehicleState {
	private Coordinates location;
	private LinkedList<Party> parties;
	private long time;
	private double battery;
	
	public VehicleState(){
		
	}
	
	public VehicleState(Coordinates location, double battery){
		this.setLocation(location);
		parties = new LinkedList<Party>();
		this.battery = battery;
		time = 0;
	}
	
	public void addParty(Party p){
		parties.add(p);
	}

	public void setLocation(Coordinates location) {
		this.location = location;
	}

	public Coordinates getLocation() {
		return location;
	}

	public LinkedList<Party> getParties() {
		return parties;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setBattery(double battery) {
		this.battery = battery;
	}

	public double getBattery() {
		return battery;
	}
	
	
}

package itaxi.messages.entities;

import itaxi.messages.coordinates.Coordinates;

import java.util.Map;

public class Station {

	private int stationId;
	private String stationDescription;
	private int numPassengers;
	private double avgWaitTime;
	private Coordinates stationPosition;
	private Map<String,Vehicle> parkedVehicles;

	public Station(){
	}
	
    public Station(int stationId, String stationDescription, int numPassengers, double avgWaitTime, 
    		Coordinates stationPosition, Map<String, Vehicle> parkedVehicles) {
    	this.stationId = stationId;
		this.stationDescription = stationDescription;
		this.parkedVehicles = parkedVehicles;
		this.numPassengers = numPassengers;
		this.avgWaitTime = avgWaitTime;
		this.stationPosition = stationPosition;
	}
    
    public void addVehicle(Vehicle vehicle){
    	this.parkedVehicles.put(vehicle.getVehicleID(), vehicle);
    }
    
    public void removeVehicle(int vehicleID){
    	this.parkedVehicles.remove(vehicleID);
    }

	public Map<String, Vehicle> getParkedVehicles() {
		return parkedVehicles;
	}

	public void setParkedVehicles(Map<String, Vehicle> parkedVehicles) {
		this.parkedVehicles = parkedVehicles;
	}
	
	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public String getStationDescription() {
		return stationDescription;
	}

	public void setStationDescription(String stationDescription) {
		this.stationDescription = stationDescription;
	}
	
	public int getNumPassengers() {
		return numPassengers;
	}

	public void setNumPassengers(int numPassengers) {
		this.numPassengers = numPassengers;
	}

	public double getAvgWaitTime() {
		return avgWaitTime;
	}

	public void setAvgWaitTime(double avgWaitTime) {
		this.avgWaitTime = avgWaitTime;
	}

	public Coordinates getStationPosition() {
		return stationPosition;
	}

	public void setStationPosition(Coordinates stationPosition) {
		this.stationPosition = stationPosition;
	}
}

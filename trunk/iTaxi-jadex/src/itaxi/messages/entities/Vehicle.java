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

	private int nparties;
		
	public int getNparties() {
		return nparties;
	}

	public void setNparties(int nparties) {
		this.nparties = nparties;
	}

	public Vehicle(){
	}
	
	public Vehicle(String vehicleID,  ComponentIdentifier agentID, double gasLevel, Coordinates initialPosition) {
		_agentID=(ComponentIdentifier)agentID;
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_position = initialPosition;
	}
	
	public Vehicle(String vehicleID, double gasLevel, Coordinates initialPosition) {
		_vehicleID = vehicleID;
		_gasLevel = gasLevel;
		_position = initialPosition;
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


	public void set_agentID(ComponentIdentifier _agentID) {
		this._agentID = (ComponentIdentifier)_agentID;
	}

	public ComponentIdentifier get_agentID() {
		return _agentID;
	}
}

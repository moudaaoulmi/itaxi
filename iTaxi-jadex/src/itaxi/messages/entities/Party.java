package itaxi.messages.entities;

import itaxi.jadex.customer.CustomerState;
import itaxi.messages.coordinates.Coordinates;

import jadex.bridge.IComponentIdentifier;

public class Party {

	//public enum PartyState{NOTBOOKED, BOOKED, FLYING};
	//private int station;
	
	private IComponentIdentifier _agentID;

	private String partyID;
	private int size;
	private Coordinates _position;
	private Coordinates _destination;
	//private long arrivalHour;
	//private PartyState bookState;
	
	private CustomerState _state;

	public Party(){
		
	}
	
	public String toString() {
		return "[PARTY] partyID=" + partyID + " size=" + size + " position=" + _position; //+ " destination=" + _destination;
	}
	
//	public Party(int station, String partyID, int size, Coordinates id, PartyState bookState) {
//		this.station = station;
//		this.partyID = partyID;
//		this.size = size;
//		this.destination = id;
//		this.setArrivalHour(new Date().getTime());
//		//this.bookState = bookState;
//	}
	
	public Party(String partyID, IComponentIdentifier agentID,  int size, Coordinates position, Coordinates destination) {
		//this.station = station;
		_agentID=agentID;
		this.partyID = partyID;
		this.size = size;
		_position = position;
		_destination = destination;
		//this.setArrivalHour(new Date().getTime());
	}
	
	public Party(String partyID,  int size, Coordinates position, Coordinates destination,CustomerState state) {
		//this.station = station;
		this.partyID = partyID;
		this.size = size;
		_position = position;
		_destination = destination;
		_state = state;
		//this.setArrivalHour(new Date().getTime());
	}
	
	public Party(String partyID,  int size, Coordinates position, Coordinates destination) {
		//this.station = station;
		this.partyID = partyID;
		this.size = size;
		_position = position;
		_destination = destination;
		//this.setArrivalHour(new Date().getTime());
	}
	
	public Party(String partyID, IComponentIdentifier agentID,  int size, int latitude, int longitude/*, PartyState bookState*/) {
		//this.station = station;
		_agentID=agentID;
		this.partyID = partyID;
		this.size = size;
		setDestination(latitude, longitude);
		//_position=new Coordinates(latitude, longitude);
		//this.setArrivalHour(new Date().getTime());
		//this.bookState = bookState;
	}
	
	public Party(String partyID,  int size, int latitude, int longitude/*, PartyState bookState*/) {
		//this.station = station;
		this.partyID = partyID;
		this.size = size;
		setDestination(latitude, longitude);
		//_position=new Coordinates(latitude, longitude);
		//this.setArrivalHour(new Date().getTime());
		//this.bookState = bookState;
	}

	public Party(String partyID, int size, int latitude, int longitude, int destLat, int destLon/*, PartyState bookState*/) {
		//this.station = station;
		this.partyID = partyID;
		this.size = size;
		setDestination(destLat, destLon);
		_position=new Coordinates(latitude, longitude);
		//this.setArrivalHour(new Date().getTime());
		//this.bookState = bookState;
	}
	
	public Party(String partyID, IComponentIdentifier agentID,  int size, int latitude, int longitude, int destLat, int destLon/*, PartyState bookState*/) {
		//this.station = station;
		_agentID=agentID;
		this.partyID = partyID;
		this.size = size;
		setDestination(destLat, destLon);
		
		//this.setArrivalHour(new Date().getTime());
		//this.bookState = bookState;
	}
	public Coordinates get_destination() {
		return _destination;
	}

	public void set_destination(Coordinates _destination) {
		this._destination = _destination;
	}
	
	public IComponentIdentifier get_agentID() {
		return _agentID;
	}

	public void set_agentID(IComponentIdentifier _agentID) {
		this._agentID = _agentID;
	}

	public Coordinates get_position() {
		return _position;
	}

	public void set_position(Coordinates _position) {
		this._position = _position;
	}
	
	public String getPartyID() {
		return partyID;
	}

	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Coordinates getDestination() {
		return _destination;
	}

	public void setDestination(Coordinates destination) {
		_destination = destination;
	}
	
	public Coordinates getPosition() {
		return _position;
	}

	public void setPosition(Coordinates position) {
		_position = position;
	}
	
	public void setDestination(int latitude, int longitude){
		this._destination = new Coordinates(latitude,longitude);
	}
	/*
	public void renewArrivalHour(){
		this.arrivalHour = new Date().getTime();
	}

	public void setArrivalHour(long arrivalHour) {
		this.arrivalHour = arrivalHour;
	}

	public long getArrivalHour() {
		return arrivalHour;
	}
	
	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}*/

	public void setState(CustomerState _state) {
		this._state = _state;
	}

	public CustomerState getState() {
		return _state;
	}

//	public PartyState getBookState() {
//		return bookState;
//	}
//
//	public void setBookState(PartyState bookState) {
//		this.bookState = bookState;
//	}
}

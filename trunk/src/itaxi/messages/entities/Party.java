package itaxi.messages.entities;

import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.exceptions.PartySizeException;

import java.util.Date;

public class Party {

	public enum PartyState{NOTBOOKED, BOOKED, FLYING};
	private int station;
	private String name;
	private int size;
	private Coordinates destination;
	private long arrivalHour;
	private PartyState bookState;

	public Party(){
		
	}
	
	public Party(int station, String name, int size, Coordinates id, PartyState bookState) throws PartySizeException{
		if(size < 0 || size > 4)
			throw new PartySizeException();
		this.station = station;
		this.name = name;
		this.size = size;
		this.destination = id;
		this.setArrivalHour(new Date().getTime());
		this.bookState = bookState;
	}
	
	public Party(int station, String name, int size, int latitude, int longitude, PartyState bookState) throws PartySizeException{
		if(size < 0 || size > 4)
			throw new PartySizeException();
		this.station = station;
		this.name = name;
		this.size = size;
		setDestination(latitude, longitude);
		this.setArrivalHour(new Date().getTime());
		this.bookState = bookState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Coordinates getDestination() {
		return destination;
	}

	public void setDestination(Coordinates destination) {
		this.destination = destination;
	}
	
	public void setDestination(int latitude, int longitude){
		this.destination = new Coordinates(latitude,longitude);
	}
	
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
	}

	public PartyState getBookState() {
		return bookState;
	}

	public void setBookState(PartyState bookState) {
		this.bookState = bookState;
	}
}

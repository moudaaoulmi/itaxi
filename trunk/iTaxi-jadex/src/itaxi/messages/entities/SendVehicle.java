package itaxi.messages.entities;

import itaxi.messages.coordinates.Coordinates;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class SendVehicle {

	private int vehicleID;
	
	private long estimatedTime;
	
	private long timeStamp;
	
	private int lastStationId;
	
	private double totalDistance;

	//parties a conter por coordinate
	private LinkedList<VehicleState> vehicleStates;
	
	public SendVehicle(){
		
	}
	
	public SendVehicle(int vehicle,Coordinates origin, double battery){
		this.vehicleID = vehicle;
		vehicleStates = new LinkedList<VehicleState>();
		vehicleStates.add(new VehicleState(origin,battery));
		timeStamp = 0;
		estimatedTime = 0;
		totalDistance = 0;
		setLastStationId(-1);
	}
	
	public double getLastBattery(){
		updateStates();
		return vehicleStates.get(vehicleStates.size()-1).getBattery();
	}
	
	public long getLastTime(){
		updateStates();
		//return (long) (totalDistance/200);
		return vehicleStates.get(vehicleStates.size()-1).getTime();
	}

	public LinkedList<VehicleState> getVehicleStates() {
		return vehicleStates;
	}

	public int getNumberOfPassengers(Coordinates c){
		int passengers = 0;
		for(VehicleState state : vehicleStates){
			if(state.getLocation().isEqual(c)){
				for(Party p : state.getParties())
					passengers += p.getSize();
			}
		}
		return passengers;
	}

	public void setVehicleID(int vehicle) {
		this.vehicleID = vehicle;
	}

	public boolean containsParty(LinkedList<Party> list, Party p){
		boolean result = false;
		for(Party party : list){
			if(party.getPartyID().compareTo(p.getPartyID())==0)
				return true;
		}
		return result;
	}
		
	public boolean containsCoordinate(Coordinates c){
		boolean result = false;
		for(VehicleState state : vehicleStates){
			if(state.getLocation().isEqual(c)){
				return true;
			}
		}
		return result;
	}
	
	public VehicleState getStateByCoordinate(Coordinates c){
		for(VehicleState state : vehicleStates){
			if(state.getLocation().isEqual(c)){
				return state;
			}
		}
		return null;
	}
	
	public double getTotalDistance(){
		updateStates();
		return totalDistance;
	}
	
	public Map<String,Party> getNotifyParties(){
		Map<String,Party> result = new TreeMap<String,Party>();
		for(VehicleState state : vehicleStates){
			for(Party p : state.getParties()){
				if(!result.containsKey(p.getPartyID()))
					result.put(p.getPartyID(),p);
			}
		}
		return result;
	}

	public int getVehicleID() {
		return vehicleID;
	}
	
	public void updateStates(){
		long time = 0;
		//-----------------------------------------------------------------------
		VehicleState previous = vehicleStates.get(0);
		double battery = previous.getBattery();
		double distance;
		totalDistance = 0;
		for(VehicleState v : vehicleStates){
			distance = previous.getLocation().distanceTo(v.getLocation());
			totalDistance += distance;
			time += timeEstimate(distance) + 3;
			battery -= dischargeBattery(distance);
			//System.out.println(vehicleID +"  has battery " + battery);
			v.setTime(time);
			v.setBattery(battery);
			previous = v;
		}
		
	}
	
	public long timeEstimate(double distance){
		return (long) distance/125;
	}
	
	//Calculate the new battery value when discharging
    public double dischargeBattery(double distance) {
    	double dischargingUnit = 10000.0*(distance/10);
    	double percentageUpdate = (10000*3600)*1.0;
    	return (dischargingUnit/percentageUpdate)*100.0;
	}
    
    public LinkedList<Coordinates> getDestinations(){
    	LinkedList<Coordinates> result = new LinkedList<Coordinates>();
    	for(VehicleState state : vehicleStates)
    		result.add(state.getLocation());
    	return result;
    }

	public void hardcoreAddParty(Party p, Coordinates coord){
		double min = -1;
		int index = 0, insertIndex = 0;
		double distance = 0;
		if(!containsCoordinate(coord)){
			VehicleState newstate = new VehicleState(coord,100);
			newstate.addParty(p);
			
			for(VehicleState state : vehicleStates){
				distance = state.getLocation().distanceTo(coord);
				if(min < 0 || min > distance){
					insertIndex = index;
					min = distance;
				}
				index++;
			}
			if(insertIndex >= vehicleStates.size() - 1)
				vehicleStates.add(newstate);
			else
				vehicleStates.add(insertIndex+1,newstate);
		}
		else
			getStateByCoordinate(coord).addParty(p);
	}
	
	public void addParty(Party p, Coordinates coord){
		if(!containsCoordinate(coord)){
			VehicleState newstate = new VehicleState(coord,0);
			newstate.addParty(p);
			vehicleStates.add(newstate);
		}
		else
			getStateByCoordinate(coord).addParty(p);
	}
	
	public void addLastDestination(Coordinates coord){
		if(!getLastCoordinates().isEqual(coord)){
			vehicleStates.add(new VehicleState(coord, 0));
			updateStates();
		}
	}
		
	public Coordinates getLastCoordinates(){
		return vehicleStates.get(vehicleStates.size()-1).getLocation();
	}
	
	public double getInitialBattery(){
		return vehicleStates.get(0).getBattery();
	}
	
	public String toString(){
		String result = "";
		for(VehicleState v : vehicleStates)
			result += v.getLocation().toString();
		return result;
	}

	public void setEstimatedTime(long estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public long getEstimatedTime() {
		return estimatedTime;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setLastStationId(int lastStationId) {
		this.lastStationId = lastStationId;
	}

	public int getLastStationId() {
		return lastStationId;
	}
}

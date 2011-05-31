package itaxi.messages.entities;


public class Statistics {

	private int passengersTransported;
	private double traveledKm;
	private double distanceSum;
	private double usedEnergy;
	private double averageTravelTime;
	private Map<Integer,Vehicle> missingVehicles;
	
	
	public Statistics() {
		
	}
	
	public Statistics(int passengersTransported, double traveledKm, double distanceSum, 
			double usedEnergy, double averageTravelTime, Map<Integer, Vehicle> missingVehicles) {
		this.passengersTransported = passengersTransported;
		this.traveledKm = traveledKm;
		this.distanceSum = distanceSum;
		this.usedEnergy = usedEnergy;
		this.averageTravelTime = averageTravelTime;
		this.missingVehicles = missingVehicles;
	}

	public int getPassengersTransported() {
		return passengersTransported;
	}

	public void setPassengersTransported(int passengersTransported) {
		this.passengersTransported = passengersTransported;
	}

	public double getTraveledKm() {
		return traveledKm;
	}

	public void setTraveledKm(double traveledKm) {
		this.traveledKm = traveledKm;
	}

	public double getDistanceSum() {
		return distanceSum;
	}

	public void setDistanceSum(double distanceSum) {
		this.distanceSum = distanceSum;
	}

	public double getUsedEnergy() {
		return usedEnergy;
	}

	public void setUsedEnergy(double usedEnergy) {
		this.usedEnergy = usedEnergy;
	}

	public double getAverageTravelTime() {
		return averageTravelTime;
	}

	public void setAverageTravelTime(double averageTravelTime) {
		this.averageTravelTime = averageTravelTime;
	}

	public Map<Integer, Vehicle> getMissingVehicles() {
		return missingVehicles;
	}

	public void setMissingVehicles(Map<Integer, Vehicle> missingVehicles) {
		this.missingVehicles = missingVehicles;
	}
}

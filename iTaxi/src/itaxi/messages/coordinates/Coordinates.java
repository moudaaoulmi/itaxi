package itaxi.messages.coordinates;

public class Coordinates {

	private int latitude;
	private int longitude;
	
	
	public Coordinates(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Coordinates() {
	}

	public int getLatitude() {
		return latitude;
	}


	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}


	public int getLongitude() {
		return longitude;
	}


	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	
	public boolean isEqual(Coordinates c){
		return (c.getLatitude() == latitude && c.getLongitude() == longitude);
	}
	
	//Calculate distance between two coordinates
	public double distanceTo(Coordinates c2) {
	    double lat1Rad = Math.toRadians(latitude/1E6);
	    double lat2Rad = Math.toRadians(c2.getLatitude()/1E6);
	    double deltaLonRad = Math.toRadians(c2.getLongitude()/1E6 - longitude/1E6);
	    return (Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
	            * Math.cos(deltaLonRad))
	            * 6371000);
	}
	
	public String toString(){
		return "( " + latitude + " , " + longitude + " )";
	}
}

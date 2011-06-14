package itaxi.messages.coordinates;

public class Coordinates {

    private static final double EARTH_RADIUS = 6378137; //meters

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
	
	
	//Calculate distance between two coordinates in meters
	public double distanceTo(Coordinates c2) {
	    final double lat1Rad = Math.toRadians(latitude/1E6);
	    final double lat2Rad = Math.toRadians(c2.getLatitude()/1E6);
	    final double deltaLonRad = Math.toRadians(c2.getLongitude()/1E6 - longitude/1E6);
	    return (Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
	            * Math.cos(deltaLonRad))
	            * EARTH_RADIUS);
	}
	
	public static double distanceTo(final int startPointLatI, final int startPointLonI,
			final int endPointLatI, final int endPointLonI) {
	    final double lat1Rad = Math.toRadians(startPointLatI/1E6);
	    final double lat2Rad = Math.toRadians(endPointLatI/1E6);
	    final double deltaLonRad = Math.toRadians(endPointLonI/1E6 - startPointLonI/1E6);
	    return (Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
	            * Math.cos(deltaLonRad))
	            * EARTH_RADIUS);
	}

	
	/**
	 * Calculates next coordinate
	 * 
	 * @param startPointLatI
	 * @param startPointLonI
	 * @param endPointLatI
	 * @param endPointLonI
	 * @param step the distance in meters to the next Coordinate
	 * @return the next Coordinate on the path to the endPoint
	 */
	public static Coordinates nextCoord(final int startPointLatI, final int startPointLonI,
										final int endPointLatI, final int endPointLonI, final int step) {
		final double startPointLat=startPointLatI/1E6;
		final double startPointLon=startPointLonI/1E6;
		final double endPointLat=endPointLatI/1E6;
		final double endPointLon=endPointLonI/1E6;
		final double course = course(startPointLat, startPointLon, endPointLat, endPointLon);
		 
		Coordinates nextCoord;
		final double distance = distanceTo(startPointLatI, startPointLonI, endPointLatI, endPointLonI);
		
		//System.err.println("!!!nextCoord!!! latitude:"+startPointLat+" longitude"+startPointLon+" goalLatitude:"+endPointLat + " goalLongitude:"+ endPointLon +" step:"+step + " distance:" + distance + " course:"+course);
		
		if(distance<=step) {
			nextCoord = new Coordinates(endPointLatI, endPointLonI);
			//System.err.println("!!!nextCoord!!!distance<=step! nextLatitude:"+nextCoord.getLatitude()+"nextLongitude"+nextCoord.getLongitude());

		} else {
			nextCoord = extrapolate(startPointLat, startPointLon, course, step);
			//System.err.println("!!!nextCoord!!!extrapolate! nextLatitude:"+nextCoord.getLatitude()+"nextLongitude"+nextCoord.getLongitude());
			}
		
		return nextCoord;
	}
	
	public Coordinates nextCoord(final int endPointLatI, final int endPointLonI, final int step) {
		return nextCoord(latitude, longitude, endPointLatI, endPointLonI, step);
	}
	
	
	/** Based on Latitude/longitude spherical geodesy formulae & scripts (c) Chris Veness 2002-2010            
	/*    www.movable-type.co.uk/scripts/latlong.html  
	 * 
	 * @param startPointLat
	 * @param startPointLon
	 * @param endPointLat
	 * @param endPointLon
	 * @return course in radians
	 */
    public static double course(final double startPointLat, final double startPointLon, final double endPointLat, final double endPointLon) {
        
        double course;
        final double lat1 = Math.toRadians(startPointLat);
        final double lat2 = Math.toRadians(endPointLat);
     
        final double  dLon = Math.toRadians(endPointLon-startPointLon);
    	final double  y = Math.sin(dLon) * Math.cos(lat2);
    	final double  x = Math.cos(lat1)*Math.sin(lat2) -
    	        Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
    	course = Math.atan2(y, x);
    	
    	return ((Math.toDegrees(course)+360)%360);
    }
    

    /**
     * This method extrapolates the endpoint of a movement with a given length from a given starting point using a given
     * course.
     *
     * @param startPointLat the latitude of the starting point in degrees, must not be {@link Double#NaN}.
     * @param startPointLon the longitude of the starting point in degrees, must not be {@link Double#NaN}.
     * @param course        the course to be used for extrapolation in degrees, must not be {@link Double#NaN}.
     * @param distance      the distance to be extrapolated in meters, must not be {@link Double#NaN}.
     *
     * @return the extrapolated point.
     */
    public static Coordinates extrapolate(final double startPointLat, final double startPointLon, final double courseD,
                                    final double distance) {
    	
    	final double lat1 = Math.toRadians(startPointLat);
        final double lon1 = Math.toRadians(startPointLon);
        final double d=distance/EARTH_RADIUS;
        double course = Math.toRadians(courseD);
        	
    	final double  lat2 = Math.asin( Math.sin(lat1)*Math.cos(d) + 
                Math.cos(lat1)*Math.sin(d)*Math.cos(course) );
    	double  lon2 = lon1 + Math.atan2(Math.sin(course)*Math.sin(d)*Math.cos(lat1), 
                       Math.cos(d)-Math.sin(lat1)*Math.sin(lat2));
    	
    	lon2 = (lon2+3*Math.PI)%(2*Math.PI) - Math.PI;
    	
        return new Coordinates((int)(Math.toDegrees(lat2)*1E6), (int)(Math.toDegrees(lon2)*1E6));
    }
	
	public String toString(){
		return "( " + latitude + " , " + longitude + " )";
	}
}

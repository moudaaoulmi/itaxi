package itaxi.messages.coordinates;

public class Coordinates {

	
    /**
     * the length of one degree of latitude (and one degree of longitude at equator) in meters.
     */
    private static final int DEGREE_DISTANCE_AT_EQUATOR = 111329;
    /**
     * the radius of the earth in meters.
     */
    private static final double EARTH_RADIUS = 6378137; //meters
    /**
     * the length of one minute of latitude in meters, i.e. one nautical mile in meters.
     */
    private static final double MINUTES_TO_METERS = 1852d;
    /**
     * the amount of minutes in one degree.
     */
    private static final double DEGREE_TO_MINUTES = 60d;
    
	
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
	    double lat1Rad = Math.toRadians(latitude/1E6);
	    double lat2Rad = Math.toRadians(c2.getLatitude()/1E6);
	    double deltaLonRad = Math.toRadians(c2.getLongitude()/1E6 - longitude/1E6);
	    return (Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
	            * Math.cos(deltaLonRad))
	            * EARTH_RADIUS);
	}
	
	public static double distanceTo(final int startPointLatI, final int startPointLonI,
			final int endPointLatI, final int endPointLonI) {
	    double lat1Rad = Math.toRadians(startPointLatI/1E6);
	    double lat2Rad = Math.toRadians(endPointLatI/1E6);
	    double deltaLonRad = Math.toRadians(endPointLonI/1E6 - startPointLonI/1E6);
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
		final double course = azimuth(startPointLat, startPointLon, endPointLat, endPointLon);
		final double stepRad = step/EARTH_RADIUS;
		
		Coordinates nextCoord;
		final double distance = distanceTo(startPointLatI, startPointLonI, endPointLatI, endPointLonI);
		if(distance<=step) {
			nextCoord = new Coordinates(endPointLatI, endPointLonI);
		} else 
			nextCoord = extrapolate(startPointLat, startPointLon, course, stepRad);
		
		return nextCoord;
	}
	
	public Coordinates nextCoord(final int endPointLatI, final int endPointLonI, final int step) {
		return nextCoord(latitude, longitude, endPointLatI, endPointLonI, step);
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
    public static Coordinates extrapolate(final double startPointLat, final double startPointLon, final double course,
                                    final double distance) {
        //
        //lat =asin(sin(lat1)*cos(d)+cos(lat1)*sin(d)*cos(tc))
        //dlon=atan2(sin(tc)*sin(d)*cos(lat1),cos(d)-sin(lat1)*sin(lat))
        //lon=mod( lon1+dlon +pi,2*pi )-pi
        //
        // where:
        // lat1,lon1  -start pointi n radians
        // d          - distance in radians Deg2Rad(nm/60)
        // tc         - course in radians

        final double crs = Math.toRadians(course);
        final double d12 = Math.toRadians(distance / MINUTES_TO_METERS / DEGREE_TO_MINUTES);

        final double lat1 = Math.toRadians(startPointLat);
        final double lon1 = Math.toRadians(startPointLon);

        final double lat = Math.asin(Math.sin(lat1) * Math.cos(d12)
            + Math.cos(lat1) * Math.sin(d12) * Math.cos(crs));
        final double dlon = Math.atan2(Math.sin(crs) * Math.sin(d12) * Math.cos(lat1),
            Math.cos(d12) - Math.sin(lat1) * Math.sin(lat));
        final double lon = (lon1 + dlon + Math.PI) % (2 * Math.PI) - Math.PI;

        return new Coordinates((int)(Math.toDegrees(lat)*1E6), (int)(Math.toDegrees(lon)*1E6));
    }

    public static double azimuth(final double lat1, final double lon1, final double lat2, final double lon2) {
    	return	Math.atan2(Math.sin(lon1-lon2)*Math.cos(lat2) , 
    					Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon1-lon2) )
    			%
    			(2*Math.PI);
    }
    

   /**
     * calculates the length of one degree of longitude at the given latitude.
     *
     * @param latitude the latitude to calculate the longitude distance for, must not be {@link Double#NaN}.
     *
     * @return the length of one degree of longitude at the given latitude in meters.
     */
    public static double longitudeDistanceAtLatitude(final double latitude) {

        final double longitudeDistanceScaleForCurrentLatitude = Math.cos(Math.toRadians(latitude));
        return DEGREE_DISTANCE_AT_EQUATOR * longitudeDistanceScaleForCurrentLatitude;
	
    }
	
	public String toString(){
		return "( " + latitude + " , " + longitude + " )";
	}
}

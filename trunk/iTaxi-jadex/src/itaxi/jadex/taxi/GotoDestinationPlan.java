package itaxi.jadex.taxi;

import java.util.ArrayList;
import java.util.List;

import com.jsambells.directions.GeoPoint;
import com.jsambells.directions.RouteAbstract;
import com.jsambells.directions.ParserAbstract.IDirectionsListener;
import com.jsambells.directions.ParserAbstract.Mode;
import com.jsambells.directions.RouteAbstract.RoutePathSmoothness;
import com.jsambells.directions.google.DirectionsAPI;
import com.jsambells.directions.google.DirectionsAPIRoute;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class GotoDestinationPlan extends Plan implements IDirectionsListener {


	private static final long serialVersionUID = -3645203582496675969L;

	private DirectionsAPIRoute route;

	@Override
	public void body() {

		final int latitude = (Integer) getBeliefbase().getBelief("latitude").getFact();
		final int longitude = (Integer) getBeliefbase().getBelief("longitude").getFact();
		
		final int destLat =  (Integer) getParameter("goalLatitude").getValue();
		final int destLon =  (Integer) getParameter("goalLongitude").getValue();
		
		List<GeoPoint> waypoints = new ArrayList<GeoPoint>();
		waypoints.add(new GeoPoint(latitude,longitude));
		waypoints.add(new GeoPoint(destLat,destLon));

		DirectionsAPI directions = new DirectionsAPI();
		directions.getDirectionsThruWaypoints(
			waypoints, 
			DirectionsAPI.Mode.DRIVING, 
			this
		);
		
		// RoutePathSmoothness.FINE;
		List<GeoPoint> geoPoints = route.getGeoPointPath(RoutePathSmoothness.ROUGH);

		for(GeoPoint geoPoint : geoPoints) {
			IGoal goal = createGoal("submove");
			goal.getParameter("goalLatitude").setValue(geoPoint.getLatitudeE6());  //38740662
			goal.getParameter("goalLongitude").setValue(geoPoint.getLongitudeE6()); //-9135561 //TODO lat lon

			dispatchSubgoalAndWait(goal);
			  //System.out.println("Dispatch move goal!");			
		}
	}
	
	/* IDirectionsListener */
	public void onDirectionsAvailable(RouteAbstract route, Mode mode) {
		this.route=(DirectionsAPIRoute)route;
	}
	
	public void onDirectionsNotAvailable() {
		// TODO Auto-generated method stub
		// Show an error?
	}

}

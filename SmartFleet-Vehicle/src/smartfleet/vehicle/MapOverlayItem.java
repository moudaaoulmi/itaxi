package smartfleet.vehicle;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapOverlayItem extends ItemizedOverlay<OverlayItem>{
	
	public enum Entity{STATION,VEHICLE,DESTINATION}; 
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private VehicleActivity vehicle;
	private Entity entity;
	
	public MapOverlayItem(Entity entity, Drawable marker, VehicleActivity vehicle) {
		  super(boundCenterBottom(marker));
		  this.vehicle = vehicle;
		  this.entity = entity;
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void removeOverlay(int index) {
		mOverlays.remove(index);
		populate();
	}
	
	public void removeOverlay(OverlayItem overlay) {
		mOverlays.remove(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
	
	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		switch (entity) {
			case DESTINATION:
				showDestination(item);
				break;
			case VEHICLE:
				vehicle.showVehicleInfo();
				break;
			case STATION:
				break;
		}
		return true;
	}
	
	private void showDestination(OverlayItem item) {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		//Create layout
		LayoutInflater inflater = (LayoutInflater) vehicle.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.destinationpoint,
		                               (ViewGroup) vehicle.findViewById(R.id.destinationpoint_root));
		
		String[] aux = item.getSnippet().split(";");
		
		//Set destination point coordinates properties
		TextView destinationpointText = (TextView) layout.findViewById(R.id.destinationpointText);
		destinationpointText.setText("GPS Point: \n" + aux[0]);
		ImageView image = (ImageView) layout.findViewById(R.id.destinationpointImage);
		image.setImageResource(R.drawable.gpspoint);

		//Set destination point coordinates properties
		if(!aux[1].contains("station")) {
			TextView destinationpointPartiesText = (TextView) layout.findViewById(R.id.destinationpointPartiesText);
			String destParties = "";
			if(aux.length > 1)
				destParties = aux[1];
			destinationpointPartiesText.setText("Destination Parties: \n" + destParties);
			ImageView image2 = (ImageView) layout.findViewById(R.id.destinationpointPartiesImage);
			image2.setImageResource(R.drawable.destinationparties);
		} else {
			TextView destinationpointPartiesText = (TextView) layout.findViewById(R.id.destinationpointPartiesText);
			String station = "Station: " + Integer.parseInt(aux[1].split(":")[1]);
			destinationpointPartiesText.setText(station);
			ImageView image2 = (ImageView) layout.findViewById(R.id.destinationpointPartiesImage);
			image2.setImageResource(R.drawable.station);
		}
		
		//Show alert dialog
		builder = new AlertDialog.Builder(vehicle);
		builder.setTitle(item.getTitle());
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();
	}
}

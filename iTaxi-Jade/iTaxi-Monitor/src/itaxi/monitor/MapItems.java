package itaxi.monitor;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;

public class MapItems extends ItemizedOverlay<MapOverlayItem>{
	
	public enum Elements{STATIONS,VEHICLES};
	private ArrayList<MapOverlayItem> mOverlays = new ArrayList<MapOverlayItem>();
	private iTaxiMainActivity monitor;
	private Elements elements;
	
	public MapItems(Elements elements, Drawable marker, iTaxiMainActivity monitor) {
		  super(boundCenterBottom(marker));
		  this.monitor = monitor;
		  this.elements = elements;
	}
	
	public boolean containsOverlay(String id) {
		for(MapOverlayItem item : mOverlays) {
			if(item.getId().compareTo(id)==0)
				return true;
		}
		return false;
	}
	
	public void addOverlay(MapOverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void removeOverlay(String id) {
		int index;
		for(index=0;index<mOverlays.size();index++) {
			MapOverlayItem item = mOverlays.get(index);
			if(item.getId().compareTo(id)==0)
				break;
		}
		mOverlays.remove(index);
		populate();
	}
	
	@Override
	protected MapOverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
	
	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		try {
			MapOverlayItem item = mOverlays.get(index);
			/*if(elements == Elements.STATIONS) {
					monitor.sendMessage(new Message(MessageType.GETSTATIONDETAILS, "" + item.getId()), true);
			} else*/ 
			//monitor.sendMessage(new Message(MessageType.GETVEHICLEDETAILS, "" + item.getId()), true);
			monitor.showVehicleDetails(item.getId());
		} catch (SecurityException e) {
			Log.d("Monitor", e.getMessage());
		}
		return true;
	}
}

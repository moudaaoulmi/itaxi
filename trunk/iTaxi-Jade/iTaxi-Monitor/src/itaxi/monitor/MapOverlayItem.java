package itaxi.monitor;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapOverlayItem extends OverlayItem{

	private String id;
	
	public MapOverlayItem(String id, GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		this.id = id;
	}

	public String getId() {
		return id;
	}
}

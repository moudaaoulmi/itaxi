package smartfleet.vehicle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import smartfleet.coordinates.Coordinates;
import smartfleet.entities.Party;
import smartfleet.entities.SendVehicle;
import smartfleet.entities.Ship;
import smartfleet.entities.Station;
import smartfleet.entities.Vehicle;
import smartfleet.entities.Vehicle.BatteryStatus;
import smartfleet.messages.Message;
import smartfleet.messages.MessageType;
import smartfleet.vehicle.MapOverlayItem.Entity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VehicleActivity extends MapActivity {
	
	//Vehicle status
	private static int SPEED = 200;
	private Vehicle vehicle;
	private Map<Integer, Station> stations;
	private FlightTask flightTask;
	private ChargeBatteryTask chargeBatteryTask;
	private ArrayList<Integer> communications;
	private String timeToDestination;
	private AlertDialog notifyPartiesDialog;		
	private Map<String, Party> parties;

	//Control vehicle battery
	private AlertDialog chargeBatteryDialog;
	private ImageView batteryImage;
	private TextView percentage;
	
	//Control map	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private MapView mapView;	
	private MapController mapController;
	private List<Overlay> mapOverlays;
	private MapOverlayItem vehicles;
	private MapOverlayItem stationItems;
	private MapOverlayItem destinationItems;
	private ImageButton zoomOut;
	private ImageButton zoomIn;
	private ImageView batteryLevelImage;
	private ImageView wirelessImage;
	private Button nextStop;
	
	//Control vehicle communication
	private CommunicationTask communicationTask;
	private String serverIp;
	private int serverPort;
	private ServerSocket serverSocket;
	private Gson gson;
	private Dialog connectServerDialog;
	
	@Override
	protected void onDestroy() {
	 	try {
	 		super.onDestroy();
	 		serverSocket.close();
		} catch (IOException e) {
			Log.d("Vehicle", e.getMessage());
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Put activity in full-screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
        
        //Get Location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        //Create Location Listener
        locationListener = new GPSListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        //Get zoom out button
        zoomOut = (ImageButton) findViewById(R.id.mapZoomOut);
        zoomOut.setOnClickListener(new ZoomOutClick());
        
        //Get zoom in button
        zoomIn = (ImageButton) findViewById(R.id.mapZoomIn);
        zoomIn.setOnClickListener(new ZoomInClick());
        
        //Get battery level image
        batteryLevelImage = (ImageView) findViewById(R.id.batteryLevel);
        
        //Get wireless image
        wirelessImage = (ImageView) findViewById(R.id.wirelessState);
        
        //Get next stop button
        nextStop = (Button) findViewById(R.id.nextStop);
        nextStop.setEnabled(false);
        nextStop.setOnClickListener(new NextStopClick());
        
        //Controls map configurations
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setSatellite(false);		
        mapController = mapView.getController();    	
        mapController.setZoom(13);
        mapController.animateTo(new GeoPoint((int)38.754525E6, (int)-9.20228E6));
        
        //Get the station and vehicle icons
        mapOverlays = mapView.getOverlays();
        vehicles = new MapOverlayItem(Entity.VEHICLE, getResources().getDrawable(R.drawable.vehicle), this);
        stationItems =  new MapOverlayItem(Entity.STATION, getResources().getDrawable(R.drawable.station), this);
        destinationItems =  new MapOverlayItem(Entity.DESTINATION, getResources().getDrawable(R.drawable.destination), this);
        	
        //Init vehicle properties
        communications = new ArrayList<Integer>();
        showConnectionDialog();
    }
    
    private class GPSListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				GeoPoint gp = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		    	vehicles.removeOverlay(0);
		    	vehicles.addOverlay(new OverlayItem(gp, "", ""));
		    	mapOverlays.remove(vehicles);
		    	mapOverlays.add(vehicles); 
		    	mapController.animateTo(gp);
		    	mapView.invalidate();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
    	
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	//Show connect to server dialog
	private void showConnectionDialog() {
		
		//Show dialog
		connectServerDialog = new Dialog(this);
		connectServerDialog.setContentView(R.layout.connectserver);
		connectServerDialog.setTitle("Connect To Server");
		connectServerDialog.show();
		
		//Add click event
		Button connect = (Button) connectServerDialog.findViewById(R.id.connectButton);
		connect.setOnClickListener(new ConnectServerClick());
	}

	//Connect to server button was clicked
	private class ConnectServerClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			
			try {
				//Get server ip address
				EditText ip = (EditText) connectServerDialog.findViewById(R.id.serverIpAdress);
				serverIp = ip.getText().toString();
				
				//Get server port
				EditText port = (EditText) connectServerDialog.findViewById(R.id.serverPort);
				serverPort = Integer.parseInt(port.getText().toString());
				
				//Get android emulator port
				EditText emuport = (EditText) connectServerDialog.findViewById(R.id.emulatorPort);
				VehicleUtil.setEmulatorPort(Integer.parseInt(emuport.getText().toString()));
			
				gson = new Gson();
				
				//Choose communication port
				if(serverSocket == null)
					serverSocket = VehicleUtil.choosePort();
				
				//Init vehicle
				sendMessageToServer(new Message(MessageType.INITVEHICLE, "" + serverSocket.getLocalPort()), true);
			
				//Close dialog
				connectServerDialog.dismiss();
				
				communicationTask = new CommunicationTask();
				communicationTask.execute();
			} catch (UnknownHostException e) {				
				Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.d("Vehicle", e.getMessage());
			} catch (IOException e) {
				Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.d("Vehicle", e.getMessage());
			} catch (SecurityException e) {				
				Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.d("Vehicle", e.getMessage());
			}
		}
	}
	
    //Sets the map properties
    private void initMap(String elements) {
    	String [] entities = elements.split(";");
     	
    	//Draws stations in map
    	Type listType = new TypeToken<ArrayList<Station>>() {}.getType();
    	LinkedList<Station> returnedStations = new Gson().fromJson(entities[0], listType);
        stations = new TreeMap<Integer, Station>();
    	for(Station station : returnedStations) {
        	GeoPoint p = new GeoPoint(station.getStationPosition().getLatitude(), station.getStationPosition().getLongitude());
    		stationItems.addOverlay(new OverlayItem(p, station.getStationDescription(), "Welcome to Smartfleet station:"));
    		stations.put(station.getStationId(), station);
    	}
    	
    	//Draw vehicle in map
    	vehicle = new Gson().fromJson(entities[1], Vehicle.class);
    	GeoPoint gpvehicle = new GeoPoint(vehicle.getPosition().getLatitude(), 
    			vehicle.getPosition().getLongitude());
    	vehicles.addOverlay(new OverlayItem(gpvehicle, "Vehicle " + this.vehicle.getVehicleID(), "Welcome to Smartfleet vehicle: "));
    	mapOverlays.add(stationItems);		
    	mapOverlays.add(vehicles);
    	mapView.setBuiltInZoomControls(false);
		mapView.setSatellite(false);
	    mapController.setZoom(12);
        mapController.animateTo(gpvehicle);
        mapView.invalidate();
        
        //Send message to simulator
        Log.d("Vehicle", "Point: " + vehicle.getPosition().getLatitude() + "," + vehicle.getPosition().getLongitude());
		sendMessageToSimulator(new Message(MessageType.UPDATEVEHICLE, new Gson().toJson(vehicle)));
        refreshBatteryStatus();
        refreshAltitude();
    }
    
    //Show charge vehicle dialog
    private void chargeVehicleDialog() {
    	Builder builder = new Builder(this);
    	LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.battery,
    	                               (ViewGroup) findViewById(R.id.battery_root));
       
    	//Init battery status image
        percentage = (TextView) layout.findViewById(R.id.percentageView);
        batteryImage = (ImageView) layout.findViewById(R.id.batteryView);
        
        //Show charge dialog
    	builder.setView(layout);
    	builder.setTitle("Vehicle " + vehicle.getVehicleID() + " is charging...");
    	chargeBatteryDialog = builder.create();
    	chargeBatteryDialog.show();
    }

    //Back button click listener
    private class ZoomOutClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			mapController.zoomOut();
		}
    }
    
    //Back button click listener
    private class ZoomInClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			mapController.zoomIn();
		}
    }
    
    //Next stop button click listener
    private class NextStopClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			AlertDialog.Builder builder;
			AlertDialog alertDialog;

			//Create layout
			LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.nextstop,
			                               (ViewGroup) findViewById(R.id.nextstop_root));

			//Set parties onboard properties
			TextView partiesText = (TextView) layout.findViewById(R.id.partiesOnboardText);
			String parties = vehicle.getParties().keySet().toString();
			if(!parties.equals("[]"))
				parties = parties.substring(1, parties.length()-1);
			else parties = "";
			partiesText.setText("Parties Onboard: \n" + parties);
			ImageView image = (ImageView) layout.findViewById(R.id.partiesOnboardImage);
			image.setImageResource(R.drawable.people);

			//Set destination parties
			TextView destinationPartiesText = (TextView) layout.findViewById(R.id.destinationPartiesText);
			LinkedList<Coordinates> destinations = vehicle.getDestinations();
			String destParties = "";
			if(destinations.size() > 1) {
				destParties = destinationParties(coordinatesToGeoPoint(destinations.get(0))); 
				destinationPartiesText.setText("Destination Parties: \n" + destParties);
				ImageView image2 = (ImageView) layout.findViewById(R.id.destinationPartiesImage);
				image2.setImageResource(R.drawable.destinationparties);
			} else {
				if(destinations.size() == 0)
					nextStop.setEnabled(false);
				else {
					destinationPartiesText.setText("Parking On Station: \n" + getStationId(destinations.get(0)));
					ImageView image2 = (ImageView) layout.findViewById(R.id.destinationPartiesImage);
					image2.setImageResource(R.drawable.station);
				}
			}
			
			//Set destination time left
			TextView timeleft = (TextView) layout.findViewById(R.id.destinationTimeLeftText);
			timeleft.setText("Time Left: " + timeToDestination);
			ImageView image5 = (ImageView) layout.findViewById(R.id.destinationTimeLeftImage);
			image5.setImageResource(R.drawable.timer);
			
			//Set battery properties
			double batteryLevel = vehicle.getBatteryLevel();
			TextView percentage = (TextView) layout.findViewById(R.id.nextPercentageView);
			percentage.setText("Battery Level: " + batteryLevel + " %");
			ImageView image3 = (ImageView) layout.findViewById(R.id.nextBatteryView);	
    		if(batteryLevel < 20)
    			image3.setImageResource(R.drawable.battery_discharging_000);
    		if((batteryLevel >= 20) && (batteryLevel < 40))
    			image3.setImageResource(R.drawable.battery_discharging_020);
    		if((batteryLevel >= 40) && (batteryLevel < 60))
    			image3.setImageResource(R.drawable.battery_discharging_040);
    		if((batteryLevel >= 60) && (batteryLevel < 80))
    			image3.setImageResource(R.drawable.battery_discharging_060);
    		if((batteryLevel >= 80) && (batteryLevel < 100))
    			image3.setImageResource(R.drawable.battery_discharging_080);
    		if(batteryLevel == 100)
    			image3.setImageResource(R.drawable.battery_discharging_100);
    		if(batteryLevel == 0)
    			image3.setImageResource(R.drawable.nocharge);
			
			//Show alert dialog
			builder = new AlertDialog.Builder(view.getContext());
			builder.setTitle("Next Stop");
			builder.setView(layout);
			alertDialog = builder.create();
			alertDialog.show();
		}
    }
    
    //Get PartyNames from destination
    private String destinationParties(GeoPoint dest) {
    	String result = "";
		for(Party party : vehicle.getParties().values()) {
			if(party.getDestination().getLatitude() == dest.getLatitudeE6()
					&& party.getDestination().getLongitude() == dest.getLongitudeE6()) {
				result += party.getName() + ",";
			}
		}
		if(result.equals(""))
			return result;
		else return result.substring(0, result.length()-1);
    }
	
	//Convert coordinates into geopoint
	private GeoPoint coordinatesToGeoPoint(Coordinates coord) {
		return new GeoPoint(coord.getLatitude(), coord.getLongitude());
	}
    
	//Show destination arrival dialog
	private void destinationReached(String parties) {
		AlertDialog.Builder builder;

		//Create layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.destinationreached,
		                               (ViewGroup) findViewById(R.id.destinationreached_root));
		
		//Set parties onboard properties
		TextView destinationPartiesText = (TextView) layout.findViewById(R.id.partiesToLeaveText);
		destinationPartiesText.setText("Parties Leaving: \n" + parties);
		ImageView image = (ImageView) layout.findViewById(R.id.destinationReachedImage);
		image.setImageResource(R.drawable.destinationparties);

		//Show alert dialog
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Destination Reached - Thank you for travelling");
		builder.setView(layout);
		notifyPartiesDialog = builder.create();
		notifyPartiesDialog.show();
		new NoticeTimer().execute();
	}
	
	//Show vehicle information dialog
	protected void showVehicleInfo() {
		AlertDialog.Builder builder;
		AlertDialog alertDialog;

		//Create layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.vehicleinfo,
		                               (ViewGroup) findViewById(R.id.vehicleinfo_root));
		
		//Set parties onboard properties
		TextView partiesText = (TextView) layout.findViewById(R.id.partiesOnboardVehicleText);
		String parties = vehicle.getParties().keySet().toString();
		if(!parties.equals("[]"))
			parties = parties.substring(1, parties.length()-1);
		else parties = "";
		partiesText.setText("Parties Onboard: \n" + parties);
		ImageView image = (ImageView) layout.findViewById(R.id.partiesOnboardVehicleImage);
		image.setImageResource(R.drawable.people);
		
		//Set battery properties
		double batteryLevel = vehicle.getBatteryLevel();
		TextView percentage = (TextView) layout.findViewById(R.id.vehicleInfoPercentageView);
		percentage.setText("Battery Level: " + batteryLevel + " %");
		ImageView image3 = (ImageView) layout.findViewById(R.id.vehicleInfoBatteryView);	
		if(batteryLevel < 20)
			image3.setImageResource(R.drawable.battery_discharging_000);
		if((batteryLevel >= 20) && (batteryLevel < 40))
			image3.setImageResource(R.drawable.battery_discharging_020);
		if((batteryLevel >= 40) && (batteryLevel < 60))
			image3.setImageResource(R.drawable.battery_discharging_040);
		if((batteryLevel >= 60) && (batteryLevel < 80))
			image3.setImageResource(R.drawable.battery_discharging_060);
		if((batteryLevel >= 80) && (batteryLevel < 100))
			image3.setImageResource(R.drawable.battery_discharging_080);
		if(batteryLevel == 100)
			image3.setImageResource(R.drawable.battery_discharging_100);
		if(batteryLevel == 0)
			image3.setImageResource(R.drawable.nocharge);
		
		//Set vehicle altitude properties
		TextView altitude = (TextView) layout.findViewById(R.id.currentAltitudeText);
		altitude.setText("Vehicle Altitude: " + vehicle.getAltitude() + " m");
		ImageView image2 = (ImageView) layout.findViewById(R.id.currentAltitudeImage);
		image2.setImageResource(R.drawable.altitude);
		
		//Show alert dialog
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Vehicle - " + vehicle.getVehicleID());
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	private boolean verifyPartyDestination(GeoPoint location) {
		for(Party party : parties.values()) {
			if(party.getDestination().isEqual(new Coordinates(location.getLatitudeE6(),
					location.getLongitudeE6())))
				return true;
		}
		return false;
	}
	
    //Update destinations in map
    private void updateDestination() {
	    	mapOverlays.remove(destinationItems);
	    	if(vehicle.getParties().size() > 0) {
	        destinationItems =  new MapOverlayItem(Entity.DESTINATION, getResources().getDrawable(R.drawable.destination), this);
	    	int i = 0;
	        for(Coordinates coord : vehicle.getDestinations()) {
	    		GeoPoint dest = new GeoPoint(coord.getLatitude(), coord.getLongitude());
	    		if(verifyPartyDestination(dest)) {
		    		if(!((i+1) == vehicle.getDestinations().size())) {
		    			destinationItems.addOverlay(new OverlayItem(dest, "Destination Point", dest.getLatitudeE6()/1E6 
		        				+ ", " + dest.getLongitudeE6()/1E6 + ";" + destinationParties(dest)));    			
		    		}
	    		}
	        	i++;
	    	}
	        if(vehicle.getDestinations().size() > 1)
	        	mapOverlays.add(destinationItems);
    	}
    	mapView.invalidate();    
    }
    
    //Update the position of the vehicle in the map
    private void updateVehiclePosition(GeoPoint gp) {
    	synchronized (vehicle) {
    		vehicle.setParked(0);
	    	vehicle.setPosition(new Coordinates(gp.getLatitudeE6(), gp.getLongitudeE6()));
	    	VehicleUtil.changeVehicleGPS(gp);
	    	
	        //Send message to simulator
			sendMessageToSimulator(new Message(MessageType.UPDATEVEHICLE, new Gson().toJson(vehicle)));
    	}
    }
    
    //Update the alitude of the vehicle
    private void refreshAltitude() {
    	TextView altitudeText = (TextView)findViewById(R.id.altitudeIconText);
    	altitudeText.setText(vehicle.getAltitude() + " m");
    }
    
    //Update the battery state icon
    private void refreshBatteryStatus() {
    	double batteryLevel = vehicle.getBatteryLevel();
    	BatteryStatus status = vehicle.getStatus();
    	if(batteryLevel > 0) {
	    	switch(status) {
		    	case CHARGING:
		    		if((chargeBatteryDialog==null) || (!chargeBatteryDialog.isShowing()))
		    			chargeVehicleDialog();
		    		if(batteryLevel < 20) {
		    			batteryImage.setImageResource(R.drawable.battery_charging_000);
		    			batteryLevelImage.setImageResource(R.drawable.mbattery_charging_000);
		    		}
		    		if((batteryLevel >= 20) && (batteryLevel < 40)) {
		    			batteryImage.setImageResource(R.drawable.battery_charging_020);
		    			batteryLevelImage.setImageResource(R.drawable.mbattery_charging_020);
		    		}
		    		if((batteryLevel >= 40) && (batteryLevel < 60)) {
		    			batteryImage.setImageResource(R.drawable.battery_charging_040);
		    			batteryLevelImage.setImageResource(R.drawable.mbattery_charging_040);
		    		}
		    		if((batteryLevel >= 60) && (batteryLevel < 80)) {
		    			batteryImage.setImageResource(R.drawable.battery_charging_060);
		    			batteryLevelImage.setImageResource(R.drawable.mbattery_charging_060);
		    		}
		    		if((batteryLevel >= 80) && (batteryLevel < 100)) {
		    			batteryImage.setImageResource(R.drawable.battery_charging_080);
		    			batteryLevelImage.setImageResource(R.drawable.mbattery_charging_080);
		    		}
		    		if(batteryLevel >= 100) {
		    			batteryImage.setImageResource(R.drawable.battery_charging_100);
		    			batteryLevelImage.setImageResource(R.drawable.mbattery_charging_100);
		    			chargeBatteryDialog.dismiss();
		    			vehicle.setStatus(BatteryStatus.DISCHARGING);
		    		}
		    		percentage.setText("Battery Level: " + batteryLevel + " %");
		    		break;
		    	case DISCHARGING:
		    		if(batteryLevel < 20)
		    			batteryLevelImage.setImageResource(R.drawable.battery_discharging_000);
		    		if((batteryLevel >= 20) && (batteryLevel < 40))
		    			batteryLevelImage.setImageResource(R.drawable.battery_discharging_020);
		    		if((batteryLevel >= 40) && (batteryLevel < 60))
		    			batteryLevelImage.setImageResource(R.drawable.battery_discharging_040);
		    		if((batteryLevel >= 60) && (batteryLevel < 80))
		    			batteryLevelImage.setImageResource(R.drawable.battery_discharging_060);
		    		if((batteryLevel >= 80) && (batteryLevel < 100))
		    			batteryLevelImage.setImageResource(R.drawable.battery_discharging_080);
		    		if(batteryLevel == 100)
		    			batteryLevelImage.setImageResource(R.drawable.battery_discharging_100);
		    		break;
		    }
    	} else {
    		batteryLevelImage.setImageResource(R.drawable.nocharge);
    	}
    }
	
    //Calculate the new battery value when charging
    private void chargeBattery() {
    	double currentPercentage = vehicle.getBatteryLevel();
    	double chargingUnit = 1000000.0;
    	double percentageUpdate = (10000*3600)*1.0;
    	double result = currentPercentage;
    	DecimalFormat format = new DecimalFormat("#.##");
    	if(vehicle.getStatus() == BatteryStatus.CHARGING) {
    		result = Double.valueOf(format.format((chargingUnit/percentageUpdate)*100.0 + currentPercentage));
    		if(result > 100)
    			result = 100;
    	}
		vehicle.setBatteryLevel(result);
	}
    
    //Calculate the new battery value when discharging
    private void dischargeBattery(double distance) {
    	double currentPercentage = vehicle.getBatteryLevel();
    	double dischargingUnit = 10000.0*(distance/10);
    	double percentageUpdate = (10000*3600)*1.0;
    	double result = currentPercentage;
    	DecimalFormat format = new DecimalFormat("#.##");
    	if(vehicle.getStatus() == BatteryStatus.DISCHARGING) {
    		result = Double.valueOf(format.format(currentPercentage - (dischargingUnit/percentageUpdate)*100.0));
    		if(result < 0)
    			result = 0;	    
    	}
		vehicle.setBatteryLevel(result);
	}
    
    //Calculate the new battery value when climbing
    private void dischargeBatteryClimb(int climbed) {
    	double currentPercentage = vehicle.getBatteryLevel();
    	double dischargingUnit = 7200.0*climbed;
    	double percentageUpdate = (10000*3600)*1.0;
    	double result = currentPercentage;
    	DecimalFormat format = new DecimalFormat("#.##");
    	if(vehicle.getStatus() == BatteryStatus.DISCHARGING) {
    		result = Double.valueOf(format.format(currentPercentage - (dischargingUnit/percentageUpdate)*100.0));
    		if(result < 0)
    			result = 0;	    
    	}
		vehicle.setBatteryLevel(result);
	}
    
    //Updates the wireless icon
    private void changeWirelessState() {
    	if(communications.size() > 0) {
			wirelessImage.setImageResource(R.drawable.wirelesson);
    	} else {
    		wirelessImage.setImageResource(R.drawable.wirelessoff);
    	}
    }
	
    //Send message to server
	private synchronized void sendMessageToServer(Message message, boolean waitResponse) throws UnknownHostException, IOException, SecurityException{
		Socket socket = new Socket(serverIp, serverPort);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		writer.write(gson.toJson(message) + "\n");
		writer.flush();
		if(waitResponse) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			handleMessage(gson.fromJson(reader.readLine(), Message.class));
			reader.close();
		}
		writer.close();
		socket.close();
	}
	
    //Send message to server
	private synchronized void sendMessageToSimulator(Message message) {
		try {
			Socket socket = new Socket(serverIp, 5000);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(new Gson().toJson(message) + "\n");
			writer.flush();
			writer.close();			
			socket.close();
		} catch (UnknownHostException e) {
			Log.d("Vehicle", e.getMessage());
		} catch (IOException e) {
			Log.d("Vehicle", e.getMessage());
		}
	}
	
    //Send message to server
	private synchronized void sendMessageToVehicle(int port, Message message) {
		try {
			Socket socket = new Socket("10.0.2.2", port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(new Gson().toJson(message) + "\n");
			writer.flush();
			writer.close();			
			socket.close();
		} catch (UnknownHostException e) {
			Log.d("Vehicle", e.getMessage());
		} catch (IOException e) {
			Log.d("Vehicle", e.getMessage());
		}
	}
	
    //Send message to server
	private synchronized void sendShipMessage(int port, Message message) {
		try {
			Socket socket = new Socket("10.0.2.2", port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(new Gson().toJson(message) + "\n");
			writer.flush();
			writer.close();			
			socket.close();
			socket = new Socket(serverIp, serverPort);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(new Gson().toJson(message) + "\n");
			writer.flush();
			writer.close();			
			socket.close();
		} catch (UnknownHostException e) {
			Log.d("Vehicle", e.getMessage());
		} catch (IOException e) {
			Log.d("Vehicle", e.getMessage());
		}
	}
	
	//Remove communication
	private void removeCommunication(int elementId) {
		int id;
		for(id=0; id < communications.size();id++) {
			if(id == elementId)
				break;
		}
		communications.remove(id-1);
	}
	
	//Handles incoming messages
	private void handleMessage(Message message) {
		switch (message.getType()) {
			case SENDVEHICLE:
				try {
					if(vehicle.getStatus() == BatteryStatus.CHARGING) {
						chargeBatteryTask.stopCharging();
					}
					flightTask = new FlightTask(gson.fromJson(message.getContent(), SendVehicle.class));
					flightTask.execute();
			        nextStop.setEnabled(true);
		    		vehicle.setParked(0);
					message.setType(MessageType.FLIGHTRESPONSE);
					sendMessageToServer(message, false);
					sendMessageToServer(new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)), false);
				} catch (UnknownHostException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (SecurityException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (IOException e) {
					Log.d("Vehicle", e.getMessage());
				}
				break;
			case INITVEHICLE:
				initMap(message.getContent());
				break;
			case COMMUNICATE:
				try {
					int id = Integer.parseInt(message.getContent());
					if(!communications.contains(id))
						communications.add(id);   	
					changeWirelessState();
					vehicle.setInfoAge(new Date().getTime());
					if(id == serverPort) {
						sendMessageToServer(new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)), false);
						vehicle.getInteractions().clear();
					}
					else {
						sendMessageToVehicle(id, new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)));
					}
				} catch (UnknownHostException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (SecurityException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (IOException e) {
					Log.d("Vehicle", e.getMessage());
				}
				break;
			case STOPCOMMUNICATE:
				if(communications.contains(Integer.parseInt(message.getContent())))
					removeCommunication(Integer.parseInt(message.getContent()));
				changeWirelessState();
				break;
			case UPDATEVEHICLE:
				Vehicle interaction = gson.fromJson(message.getContent(), Vehicle.class);
				Map<Integer, Vehicle> interactions = vehicle.getInteractions();
				for(Vehicle v : interaction.getInteractions().values()) {
					if(v.getVehicleID() != vehicle.getVehicleID()) {
						if(interactions.containsKey(v.getVehicleID())) {
							Vehicle mine = interactions.get(v.getVehicleID());
							if(mine.getInfoAge() <= v.getInfoAge()) {
								interactions.put(v.getVehicleID(), v);
							}
						} else {
							interactions.put(v.getVehicleID(), v);
						}
					}
				}
				interactions.put(interaction.getVehicleID(), interaction);
				break;
			case CLIMBALTITUDE:
				int value = Integer.parseInt(message.getContent());
				vehicle.setAltitude(vehicle.getAltitude()+value);
				dischargeBatteryClimb(value);
				refreshBatteryStatus();
				refreshAltitude();
				break;
			case DESCENDINGALTITUDE:
				value = Integer.parseInt(message.getContent());
				vehicle.setAltitude(value);
				refreshAltitude();
				break;
			case POWER:
				try {
					if(vehicle.getStatus() == BatteryStatus.DISCHARGING && vehicle.getParked() != 0) {
						chargeBatteryTask = new ChargeBatteryTask();
						chargeBatteryTask.execute();
						sendMessageToServer(new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)), false);
					}
				} catch (UnknownHostException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (SecurityException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (IOException e) {
					Log.d("Vehicle", e.getMessage());
				}
				break;
			case FORCEBATTERYFAILURE:
				vehicle.setAltitude(0);
				vehicle.setBatteryLevel(0);
				break;
		}
	}
	
    //Task for communication
	private class CommunicationTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				while(true) {
					//Accept connection in available port
					Socket socket = serverSocket.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					publishProgress(reader.readLine());
					reader.close();
				}
			} catch (IOException e) {
				Log.d("Vehicle", e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String ... params) {
			handleMessage(gson.fromJson(params[0], Message.class));
		}
	}

    //Charge battery
	private class ChargeBatteryTask extends AsyncTask<Void, Void, Void> {
		
		//Stop charging vehicle
		private boolean stop;
		
		public ChargeBatteryTask() {
			stop = false;
			vehicle.setStatus(BatteryStatus.CHARGING);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				double initialbattery = vehicle.getBatteryLevel();
				while(!stop) {
					Thread.sleep(1000);
					chargeBattery();
					if((vehicle.getBatteryLevel()-initialbattery) >= 5) {
						try {
							initialbattery = vehicle.getBatteryLevel();
							sendMessageToServer(new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)), false);
						} catch (UnknownHostException e) {
							Log.d("Vehicle",e.getMessage());
						} catch (SecurityException e) {
							Log.d("Vehicle",e.getMessage());
						} catch (IOException e) {
							Log.d("Vehicle",e.getMessage());
						}
					}
	    			if(vehicle.getBatteryLevel() == 100) {
						try {
							stopCharging();
							sendMessageToServer(new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)), false);
						} catch (UnknownHostException e) {
							Log.d("Vehicle",e.getMessage());
						} catch (SecurityException e) {
							Log.d("Vehicle",e.getMessage());
						} catch (IOException e) {
							Log.d("Vehicle",e.getMessage());
						}
	    			}
	    			publishProgress();
				}
			} catch (InterruptedException e) {
				Log.d("Vehicle",e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void ... params) {
			refreshBatteryStatus();
		}
		
		//Stop charging vehicle
		public void stopCharging() {
			vehicle.setStatus(BatteryStatus.DISCHARGING);
			if(chargeBatteryDialog.isShowing())
				chargeBatteryDialog.dismiss();
			stop = true;
		}
	}
	
	//Return station identifier
	private int getStationId(Coordinates stationCoord) {
		for(Station station : stations.values()) {
			if(station.getStationPosition().isEqual(stationCoord))
				return station.getStationId();
		}
		return -1;
	}
	
	//Station parties
	private Map<String, Party> getStationParties(Map<String, Party> parties, Coordinates location) {
		Map<String, Party> ps = new TreeMap<String, Party>();
		for(Party party : parties.values()) {
			Station station = stations.get(party.getStation());
			if(station.getStationPosition().isEqual(location))
				ps.put(party.getName(), party);
		}
		return ps;
	}	
	
	//Simulate vehicle flight
	private class FlightTask extends AsyncTask<Void, GeoPoint, Void> {

		private boolean stop;
		private String partiesToLeave;

		public FlightTask(SendVehicle v) {
			stop = false;
			partiesToLeave = "";
			vehicle.setStatus(BatteryStatus.DISCHARGING);
			vehicle.setAltitude(200);
			parties = v.getNotifyParties();
			vehicle.setDestinations(v.getDestinations());
			vehicle.setLastknownpath(v.toString());
			vehicle.setParties(new TreeMap<String, Party>());
		}
		
		@Override
		protected Void doInBackground(Void... args) {
			try {
				GeoPoint currentPosition = new GeoPoint(vehicle.getPosition().getLatitude(), vehicle.getPosition().getLongitude());
				double initialDistance = 0;
				if(vehicle.getParties().size() == 0) {
					vehicle.setParties(getStationParties(parties, vehicle.getPosition()));
					if(vehicle.getParties().size() > 0) {
						Ship ship = new Ship(vehicle.getVehicleID());
						ship.setParties(vehicle.getParties());
						sendShipMessage(getStationId(vehicle.getPosition()), 
								new Message(MessageType.SHIP, gson.toJson(ship)));
					}
				}
				while(!stop) {			
					double distance = calculateDistance(currentPosition, coordinatesToGeoPoint(vehicle.getDestinations().get(0)));
					if(initialDistance == 0)
						initialDistance = distance;
					if(distance < SPEED) {
						currentPosition = coordinatesToGeoPoint(vehicle.getDestinations().get(0));
						partiesToLeave = removeParties(currentPosition);
						vehicle.getDestinations().remove(0);
						dischargeBattery(distance);
						initialDistance = 0;						
						calculateDestinationTimeLeft(0);
						publishProgress(currentPosition);
						Thread.sleep(3000);
						if(vehicle.getParties().size() == 0) {
							vehicle.setParties(getStationParties(parties, vehicle.getPosition()));
							if(vehicle.getParties().size() > 0) {
								Ship ship = new Ship(vehicle.getVehicleID());
								ship.setParties(vehicle.getParties());
								sendShipMessage(getStationId(vehicle.getPosition()), 
										new Message(MessageType.SHIP, gson.toJson(ship)));
							}
						}
					}
					else {
						currentPosition = calculateNewPosition(currentPosition, coordinatesToGeoPoint(vehicle.getDestinations().get(0)));
						dischargeBattery(calculateDistance(currentPosition, coordinatesToGeoPoint(vehicle.getPosition())));
						calculateDestinationTimeLeft(distance);
						publishProgress(currentPosition);
						Thread.sleep(1000);
					}
				}
			} catch (InterruptedException e) {
				Log.d("Vehicle", e.getMessage());
			}
			return null;
		}
		
		//Calculates the new position of the vehicle by moving it SPEED meters
		private GeoPoint calculateNewPosition(GeoPoint gp, GeoPoint dest) {
			float meterPixels = mapView.getProjection().metersToEquatorPixels((float) SPEED);
			Point point = mapView.getProjection().toPixels(gp, new Point());
			Point point2 = mapView.getProjection().toPixels(dest, new Point());
			double angle = Math.atan2(point2.y-point.y, point2.x-point.x);
			point.x += Math.round((meterPixels*Math.cos(angle)));
			point.y += Math.round((meterPixels*Math.sin(angle)));
			return mapView.getProjection().fromPixels(point.x, point.y);
		}
		
		//Calculates the distance between two GeoPoints
		private double calculateDistance(GeoPoint src, GeoPoint dest) {
		    double lat1Rad = Math.toRadians(src.getLatitudeE6()/1E6);
		    double lat2Rad = Math.toRadians(dest.getLatitudeE6()/1E6);
		    double deltaLonRad = Math.toRadians(dest.getLongitudeE6()/1E6 - src.getLongitudeE6()/1E6);
		    return (Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
		            * Math.cos(deltaLonRad))
		            * 6371000);
		}
		
		//Remove parties when arriving at destination
		private String removeParties(GeoPoint destination) {
			Map<String, Party> ps = new TreeMap<String, Party>(vehicle.getParties());
			ArrayList<String> partiesLeaving = new ArrayList<String>();
			for(Party party : ps.values()) {
				if(party.getDestination().getLatitude() == destination.getLatitudeE6()
						&& party.getDestination().getLongitude() == destination.getLongitudeE6()) {
					vehicle.removeParty(party.getName());
					parties.remove(party.getName());
					partiesLeaving.add(party.getName());
				}
			}
			if(partiesLeaving.size()>0)
				return partiesLeaving.toString().substring(1, partiesLeaving.toString().length()-1);
			else return new String("");
		}
		
		//Calculate destination time left
		private void calculateDestinationTimeLeft(double distanceLeft) {
			long seconds = Math.round(distanceLeft/SPEED);
			int h = (int)seconds/3600;
			int min = (int)seconds/60;
			int sec = (int)seconds%60;
			timeToDestination = h + "h:" + min + "m:" + sec + "s";
		}
		
		@Override
		protected void onProgressUpdate(GeoPoint ... params) {
			refreshAltitude();
			refreshBatteryStatus();
			updateDestination();
			updateVehiclePosition(params[0]);				
			if(!partiesToLeave.equals("")) {
				destinationReached(partiesToLeave);
				partiesToLeave = "";
			}
			if(vehicle.getDestinations().size()==0) {
		    	vehicle.setAltitude(0);
				vehicle.setParked(getStationId(vehicle.getPosition()));
				nextStop.setEnabled(false);
				
		        //Send message to simulator
				sendMessageToSimulator(new Message(MessageType.UPDATEVEHICLE, new Gson().toJson(vehicle)));
				
		        //Send message to server
				try {
					sendMessageToServer(new Message(MessageType.ENDFLIGHT, ""+vehicle.getVehicleID()), false);
					sendMessageToServer(new Message(MessageType.UPDATEVEHICLE, gson.toJson(vehicle)), false);
				} catch (UnknownHostException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (SecurityException e) {
					Log.d("Vehicle", e.getMessage());
				} catch (IOException e) {
					Log.d("Vehicle", e.getMessage());
				}
				stopFlight();
			} else  {
				if(vehicle.getBatteryLevel() == 0)
					stopFlight();
			}
		}
		
		//Stop discharging vehicle
		public void stopFlight() {
			stop = true;
		}
	}
	
	private class NoticeTimer extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... a) {
			try {
				Thread.sleep(2000);
				publishProgress();
			} catch (InterruptedException e) {
				Log.d("Station", e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void ... params) {
			notifyPartiesDialog.dismiss();
		}
	}
}
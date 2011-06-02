package itaxi.monitor;

import itaxi.communications.messages.Message;
import itaxi.messages.entities.Station;
import itaxi.messages.entities.Statistics;
import itaxi.messages.entities.Vehicle;
import itaxi.monitor.MapItems.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class iTaxiMainActivity extends MapActivity {
	
	//Manage Map
	private MapView mapView;	
	private MapController mapController;
	private List<Overlay> mapOverlays;
	private MapItems vehicleItems;
	//private MapItems stationItems;
	private ImageButton zoomOut;
	private ImageButton zoomIn;
	private Button mapChangeView;
	private Button overallStatistics;
	
	private TreeMap<String,Vehicle> vehicles;
	
	private Statistics statistics;
	
	//Save connection to server
	//private Dialog connectServerDialog;
	
	//Manage connection to server
	private ServerSocket serverSocket;
	//private String serverIp;
	//private int serverPort;
	private ServerConnectionTask connectionTask;
	private Gson gson;
	
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
        setContentView(R.layout.maplayout);

        gson = new Gson();
        
        vehicles = new TreeMap<String,Vehicle>();
        statistics = new Statistics(0,0,0,0,0,null);
        
        //Get zoom out button
        zoomOut = (ImageButton) findViewById(R.id.mapZoomOut);
        zoomOut.setOnClickListener(new ZoomOutClick());
        
        //Get zoom in button
        zoomIn = (ImageButton) findViewById(R.id.mapZoomIn);
        zoomIn.setOnClickListener(new ZoomInClick());
        
        //Get change view button
        mapChangeView = (Button) findViewById(R.id.mapChangeView);
        mapChangeView.setOnClickListener(new ChangeViewClick());
        
        //Get overall statistics button
        overallStatistics = (Button) findViewById(R.id.overallStatistics);
        overallStatistics.setOnClickListener(new OverallStatisticsClick());
        
        //Controls map configurations
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setSatellite(false);
        mapController = mapView.getController();
        
        //Get the station and vehicle icons
        mapOverlays = mapView.getOverlays();
        //stationItems = new MapItems(Elements.STATIONS, getResources().getDrawable(R.drawable.station), this);
        vehicleItems = new MapItems(Elements.VEHICLES, getResources().getDrawable(R.drawable.vehicle), this);
        
        //Init map information
        init();
	}
	
	private void addVehicle(Vehicle vec){
		if(!vehicles.containsKey(vec.getVehicleID())){
			insertOnMap(vec);
			vehicles.put(vec.getVehicleID(), vec);
			
		}
		else{
			vehicles.remove(vec.getVehicleID());
			vehicles.put(vec.getVehicleID(), vec);			
		}
	}
	
	private Vehicle getVehicle(String id){
		if(vehicles.containsKey(id))
			return vehicles.get(id);
		return null;
	}
	
	
	//Show monitor menu options
	private void init() {

		//Set map properties
    	mapController.setZoom(13);
        mapController.animateTo(new GeoPoint((int)38.754525E6, (int)-9.20228E6));
        
        try {
			serverSocket = new ServerSocket(8002);
		} catch (IOException e) {
			Log.d("Monitor", "Socket IO exception!");
		}
		
		connectionTask = new ServerConnectionTask();
		connectionTask.execute();
        
		//Get connection to server
		//showConnectionDialog();
	}
	
	private void insertOnMap(Vehicle vec){
		GeoPoint p = new GeoPoint(vec.getPosition().getLatitude(), vec.getPosition().getLongitude());
		
		if(vehicleItems.containsOverlay(vec.getVehicleID()))
    		vehicleItems.removeOverlay(vec.getVehicleID());
		
		vehicleItems.addOverlay(new MapOverlayItem(vec.getVehicleID(), p, "Vehicle " + vec.getVehicleID(), "Welcome to iTaxi services!"));
		
    	mapOverlays.remove(vehicleItems);
    	mapOverlays.add(vehicleItems); 
    	mapView.invalidate();
        
	}
	
	//Sets the map properties
    /*private void initMap(String elements) {
    	
    	String [] entities = elements.split(";");
 	
    	//Draws stations in map
    	Type listType = new TypeToken<ArrayList<Station>>() {}.getType();
    	/*LinkedList<Station> returnedStations = gson.fromJson(entities[0], listType);
    	for(Station station : returnedStations) {
        	GeoPoint p = new GeoPoint(station.getStationPosition().getLatitude(), station.getStationPosition().getLongitude());
    		stationItems.addOverlay(new MapOverlayItem(station.getStationId(), p, station.getStationDescription(), "Welcome to Smartfleet station:"));
    	}

    	//Draws vehicles in map
    	if(entities.length > 1) {
    		listType = new TypeToken<ArrayList<Vehicle>>() {}.getType();
        	LinkedList<Vehicle> returnedVehicles = gson.fromJson(entities[1], listType);
			for(Vehicle vehicle : returnedVehicles) {
				if(vehicle.getParked() == 0) {
			    	GeoPoint p = new GeoPoint(vehicle.getPosition().getLatitude(), vehicle.getPosition().getLongitude());
					vehicleItems.addOverlay(new MapOverlayItem(vehicle.getVehicleID(), p, "Vehicle " + vehicle.getVehicleID(), "Welcome to iTaxi services!"));
				}
			}
    	}
    	
    	/*if(stationItems.size() > 0)
    		mapOverlays.add(stationItems);
    	if(vehicleItems.size() > 0)
    		mapOverlays.add(vehicleItems);
        mapView.invalidate();
    }
	*/
	
    //Update the position of the vehicle in the map
    private void updateVehiclePosition(Vehicle vehicle) {
    	GeoPoint gp = new GeoPoint(vehicle.getPosition().getLatitude(), vehicle.getPosition().getLongitude());
    	if(vehicleItems.containsOverlay(vehicle.getVehicleID()))
    		vehicleItems.removeOverlay(vehicle.getVehicleID());
		vehicleItems.addOverlay(new MapOverlayItem(vehicle.getVehicleID(), gp, "", ""));
    	mapOverlays.remove(vehicleItems);
    	mapOverlays.add(vehicleItems); 
    	mapView.invalidate();
    }
    
	/*//Show connect to server dialog
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
				serverPort =  Integer.parseInt(port.getText().toString());
				
				
				//Get android emulator port
				EditText emuport = (EditText) connectServerDialog.findViewById(R.id.emulatorPort);
				MonitorUtil.redirEmulatorPort(Integer.parseInt(emuport.getText().toString()), serverPort+1);
				
							
				//Get initial station and vehicles from server
				Message message = new Message(MessageType.MONITORMAP);
				sendMessage(message, true);				
				
				//Server connection parameters
				connectServerDialog.dismiss();
				serverSocket = new ServerSocket(serverPort+1);
				connectionTask = new ServerConnectionTask();
				connectionTask.execute();
			} catch (UnknownHostException e) {				
				Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.d("Monitor", e.getMessage());
			} catch (IOException e) {
				Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.d("Monitor", e.getMessage());
			} catch (SecurityException e) {				
				Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.d("Monitor", e.getMessage());
			}
		}
	}
	*/
	
	//Send messages to server
	/*public synchronized void sendMessage(Message message, boolean waitResponse) throws IOException, SecurityException, UnknownHostException {
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
	*/
    
	//Handles incoming messages
	private void handleMessage(Message message) {
		switch (message.getType()) {
			case UPDATEVEHICLE:
				Log.d("Monitor", "RECEIVED UPDATEVEHICLE");
				Vehicle vec = new Gson().fromJson(message.getContent(), Vehicle.class);
				addVehicle(vec);
				updateVehiclePosition(vec);
				Log.d("Monitor", "UPDATED");
				break;
			default:
				Log.d("Monitor", "RECEIVED DEFAULT");
				break;
		}
	}
	
	//Receives messages from server in the background
	private class ServerConnectionTask extends AsyncTask<Void, Message, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String msg = reader.readLine();
					publishProgress(gson.fromJson(msg, Message.class));
					reader.close();
					socket.close();
				}
			} catch (IOException e) {
				Log.d("Monitor", e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Message... messages) {
			handleMessage(messages[0]);
		}
	}
	
	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    //Overall statistics click listener
    private class OverallStatisticsClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			showOverallStatistics();
		}
    }
    
    //Zoom Out click listener
    private class ZoomOutClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			mapController.zoomOut();
		}
    }
    
    //Zoom In click listener
    private class ZoomInClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			mapController.zoomIn();
		}
    }
    
    //Change view button click listener
    private class ChangeViewClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			String currentView = (String)mapChangeView.getText();
			if(currentView.equals("Satellite")) {
				mapView.setSatellite(true);
				mapChangeView.setText("Map");
			} else {
				mapView.setSatellite(false);
				mapChangeView.setText("Satellite");
			}
		}
    }
    
    //Show overall statistics dialog
    private void showOverallStatistics() {
    	AlertDialog.Builder builder;
		AlertDialog alertDialog;

		//Create layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.statistics,
		                               (ViewGroup) findViewById(R.id.statistics_root));

		//Set number of passengers transported
		TextView numberPassengers = (TextView) layout.findViewById(R.id.numberPassengersStatisticsText);
		numberPassengers.setText("Passengers Transported: " + statistics.getPassengersTransported());
		ImageView image = (ImageView) layout.findViewById(R.id.numberPassengersStatisticsImage);
		image.setImageResource(R.drawable.people);
		
		//Set total traveled km
		TextView traveledKm = (TextView) layout.findViewById(R.id.traveledKmText);
		traveledKm.setText("Traveled Km: " + statistics.getTraveledKm());
		ImageView image2 = (ImageView) layout.findViewById(R.id.traveledKmImage);
		image2.setImageResource(R.drawable.kilometer);
		
		//Set sum of straight line distance
		TextView straightDistance = (TextView) layout.findViewById(R.id.straightLineText);
		straightDistance.setText("Straight Line Distance: " + statistics.getDistanceSum());
		ImageView image3 = (ImageView) layout.findViewById(R.id.straightLineImage);
		image3.setImageResource(R.drawable.kilometer);
		
		//Set used energy
		TextView usedEnergy = (TextView) layout.findViewById(R.id.usedEnergyText);
		usedEnergy.setText("Used Energy: " + statistics.getUsedEnergy());
		ImageView image4 = (ImageView) layout.findViewById(R.id.usedEnergyImage);
		image4.setImageResource(R.drawable.power);
		
		//Set average travel time
		TextView averageTravel = (TextView) layout.findViewById(R.id.averageTravelText);
		averageTravel.setText("Average Travel Time: " + statistics.getAverageTravelTime());
		ImageView image5 = (ImageView) layout.findViewById(R.id.averageTravelImage);
		image5.setImageResource(R.drawable.timer);
		
		/*
		//Set missing vehicles
		TextView missingVehicles = (TextView) layout.findViewById(R.id.missingVehiclesText);
		String mVehicles = statistics.getMissingVehicles().keySet().toString();
		if(mVehicles.equals("[]"))
			mVehicles = "";
		else mVehicles = mVehicles.substring(1,mVehicles.length()-1);
		missingVehicles.setText("Missing Vehicles: " + mVehicles);
		ImageView image6 = (ImageView) layout.findViewById(R.id.missingVehiclesImage);
		image6.setImageResource(R.drawable.vehiclelow);
		*/
		
		//Show alert dialog
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Overall Statistics");
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();
    }
    
    //Show station details dialog
    public void showVehicleDetails(String vehicleId) {
    	AlertDialog.Builder builder;
		AlertDialog alertDialog;
		
		Vehicle vehicle = getVehicle(vehicleId);
		
		if(vehicle == null){
			Log.d("Monitor","vehicle not found");
			return;
		}

		//Create layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.vehicledetails,
		                               (ViewGroup) findViewById(R.id.vehicledetails_root));

		//Set last known number of passengers
		TextView numberPassengers = (TextView) layout.findViewById(R.id.numberPassengersVehicleText);
		numberPassengers.setText("Passengers Transporting: " + vehicle.getPassengers());
		ImageView image = (ImageView) layout.findViewById(R.id.numberPassengersVehicleImage);
		image.setImageResource(R.drawable.people);
		
		//Set battery level properties
		double batteryLevel = vehicle.getBatteryLevel();
		TextView percentage = (TextView) layout.findViewById(R.id.batteryLevelVehicleText);
		percentage.setText("Battery Level: " + batteryLevel + " %");
		ImageView image2 = (ImageView) layout.findViewById(R.id.batteryLevelVehicleImage);	
		if(batteryLevel < 20)
			image2.setImageResource(R.drawable.battery_discharging_000);
		if((batteryLevel >= 20) && (batteryLevel < 40))
			image2.setImageResource(R.drawable.battery_discharging_020);
		if((batteryLevel >= 40) && (batteryLevel < 60))
			image2.setImageResource(R.drawable.battery_discharging_040);
		if((batteryLevel >= 60) && (batteryLevel < 80))
			image2.setImageResource(R.drawable.battery_discharging_060);
		if((batteryLevel >= 80) && (batteryLevel < 100))
			image2.setImageResource(R.drawable.battery_discharging_080);
		if(batteryLevel == 100)
			image2.setImageResource(R.drawable.battery_discharging_100);
		if(batteryLevel == 0)
			image2.setImageResource(R.drawable.nocharge);
		
		//Set last known travel path
		TextView travelPath = (TextView) layout.findViewById(R.id.travelPathText);
		travelPath.setText("Travel Path: " + vehicle.getLastknownpath());
		ImageView image3 = (ImageView) layout.findViewById(R.id.travelPathImage);
		image3.setImageResource(R.drawable.travelpath);
		
		//Set last known interactions
		TextView interactions = (TextView) layout.findViewById(R.id.interactionsText);
		interactions.setText("Interactions: " + vehicle.getOtherVehicles());
		ImageView image4 = (ImageView) layout.findViewById(R.id.interactionsImage);
		image4.setImageResource(R.drawable.interactions);
		
		//Set information age
		TextView infoAge = (TextView) layout.findViewById(R.id.informationAgeText);
		double age = (new Date().getTime() - vehicle.getInfoAge())/1000;
		int h = (int)age/3600;
		int min = (int)age/60;
		int sec = (int)age%60;
		infoAge.setText("Information Age: " + h + "h:" + min + "m:" + sec + "s");
		ImageView image5 = (ImageView) layout.findViewById(R.id.informationAgeImage);
		image5.setImageResource(R.drawable.infoage);
		
		//Show alert dialog
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Vehicle " + vehicle.getVehicleID() + " - Details");
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();
    }
    
    /*
    //Show vehicle details dialog
    private void showStationDetails(Station station) {
    	AlertDialog.Builder builder;
		AlertDialog alertDialog;

		//Create layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.stationdetails,
		                               (ViewGroup) findViewById(R.id.stationdetails_root));

		//Set number of passengers waiting
		TextView numberPassengers = (TextView) layout.findViewById(R.id.numberPassengersWaitingText);
		numberPassengers.setText("Passengers Waiting: " + station.getNumPassengers());
		ImageView image = (ImageView) layout.findViewById(R.id.numberPassengersWaitingImage);
		image.setImageResource(R.drawable.people);
			
		//Set average travel time
		TextView averageTravel = (TextView) layout.findViewById(R.id.averageWaitTimeText);
		averageTravel.setText("Average Wait Time: " + station.getAvgWaitTime());
		ImageView image5 = (ImageView) layout.findViewById(R.id.averageWaitTimeImage);
		image5.setImageResource(R.drawable.timer);
		
		//Set missing vehicles
		TextView parkedVehicles = (TextView) layout.findViewById(R.id.vehiclesParkedText);
		String pVehicles = station.getParkedVehicles().keySet().toString();
		if(pVehicles.equals("[]"))
			pVehicles = "";
		else pVehicles = pVehicles.substring(1,pVehicles.length()-1);
		parkedVehicles.setText("Parked Vehicles: " + pVehicles);
		ImageView image6 = (ImageView) layout.findViewById(R.id.vehiclesParkedImage);
		image6.setImageResource(R.drawable.vehiclelow);
		
		//Show alert dialog
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Station " + station.getStationDescription() + " - Details");
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();
    }
    */
}

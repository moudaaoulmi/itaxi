package itaxi.monitor;

import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.jadex.customer.CustomerState;
import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;
import itaxi.monitor.MapItems.Elements;
import jadex.bridge.ComponentIdentifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.gson.Gson;

public class iTaxiMainActivity extends MapActivity {
	
	//distance between vehicle and party in meters
	private static int NEARBY_DISTANCE = 35;
	 
	//initial ports
	private final static int CUSTOMER_PORTS = 55000;
	private final static int TAXI_PORTS = 57000;
	
	private static int EMULATORPORT = 5554;
	private static String EMULATORIP = "10.0.2.2";
	private static int MONITORPORT = 8002;
	
	//Manage Map
	private MapView mapView;	
	private MapController mapController;
	private List<Overlay> mapOverlays;
	
	private MapItems vehicleRedItems;
	private MapItems vehicleGreenItems;
	//private MapItems partyItems;
	
	private MapItems partyHappyItems;
	private MapItems partyImpacientItems; 
	private MapItems partyAngryItems;

	//private MapItems stationItems;
	private ImageButton zoomOut;
	private ImageButton zoomIn;
	private Button mapChangeView;
	private Button overallStatistics;
	
	private HashMap<String,Vehicle> vehicles  = new HashMap<String,Vehicle>();;
	private HashMap<String,Party> parties  = new HashMap<String,Party>();;
	//private HashMap<String,Integer> partiesSocks  = new HashMap<String,Integer>();

	private HashSet<String> waitingParties = new HashSet<String>();
	private HashSet<String> roamingVehicles = new HashSet<String>();
	
	//private Statistics statistics;
	
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
     
        //statistics = new Statistics(0,0,0,0,0,null);
        
        //Get zoom out button
        zoomOut = (ImageButton) findViewById(R.id.mapZoomOut);
        zoomOut.setOnClickListener(new ZoomOutClick());
        
        //Get zoom in button
        zoomIn = (ImageButton) findViewById(R.id.mapZoomIn);
        zoomIn.setOnClickListener(new ZoomInClick());
        
        //Get change view button
        mapChangeView = (Button) findViewById(R.id.mapChangeView);
        mapChangeView.setOnClickListener(new ChangeViewClick());
        
        //Controls map configurations
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setSatellite(false);
        mapController = mapView.getController();
        
        //Get the station and vehicle icons
        mapOverlays = mapView.getOverlays();
        //stationItems = new MapItems(Elements.STATIONS, getResources().getDrawable(R.drawable.station), this);
        vehicleGreenItems = new MapItems(Elements.VEHICLES, getResources().getDrawable(R.drawable.vehicle_green), this);
        vehicleRedItems = new MapItems(Elements.VEHICLES, getResources().getDrawable(R.drawable.vehicle_red), this);
        //partyItems = new MapItems(Elements.PARTIES, getResources().getDrawable(R.drawable.party), this);
        
        partyHappyItems = new MapItems(Elements.PARTIESHAPPY, getResources().getDrawable(R.drawable.partyhappy), this);
        partyImpacientItems = new MapItems(Elements.PARTIESIMPACIENT, getResources().getDrawable(R.drawable.partyimpacient), this);
        partyAngryItems = new MapItems(Elements.PARTIESANGRY, getResources().getDrawable(R.drawable.partyangry), this);
        
        //Init map information
        init();
	}
	
	//Show monitor menu options
	private void init() {
		Log.d("Monitor","INITIALIZING MONITOR\n\n");
		//Set map properties
    	mapController.setZoom(16);
        mapController.animateTo(new GeoPoint((int)38742369, (int)-9140110));
        
        addStations();
        
		//serverSocket = choosePort();
        
		try {
			//redirEmulatorPort(MONITORPORT); //TODO redir
			serverSocket = new ServerSocket(MONITORPORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Log.d("INIT", e.getStackTrace().toString());
			//e.printStackTrace();
		} 
		connectionTask = new ServerConnectionTask();
		connectionTask.execute();
        
		//Get connection to server
		//showConnectionDialog();
	}

	//Choose port for accepting connections
	/*protected static ServerSocket choosePort() {
		ServerSocket socket;
		int port = INITIALPORT;
		while(true) {
			try {
				redirEmulatorPort(port);
				socket = new ServerSocket(port);
				Log.d("Vehicle", "Accepting connections (" + EMULATORPORT + ") in port: " + port);
				break;
			} catch (IOException e) {
				port++;
				continue;
			}
		}
		return socket;
	}*/
	
	//Apply redir instruction to the emulator using telnet
	protected static void redirEmulatorPort(int port) throws IOException {
		
		// Initiate a telnet connection to the emulator
		TelnetClient tc = new TelnetClient();
		tc.connect(EMULATORIP, EMULATORPORT);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(tc.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(tc.getInputStream()));
		
		// Remove first line of the emulator console 
		reader.readLine();
		reader.readLine();
		
		// Apply redir command
		writer.write("redir del tcp:" + port + "\n");
		writer.flush();
		reader.readLine();
		writer.write("redir add tcp:" + port + ":" + port + "\n");
		writer.flush();
		String response = reader.readLine();
		tc.disconnect();
		if(!response.equals("OK"))
			Log.d("Monitor","TELNET REDIR : Port 8002 was already registered!");
		else
			Log.d("Monitor","TELNET REDIR : Port 8002 was registered!");
		
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
	
	private void addParty(Party part){
		if(!parties.containsKey(part.getPartyID())){
			insertOnMap(part);
			parties.put(part.getPartyID(), part);
		}
		else{
			parties.remove(part.getPartyID());
			parties.put(part.getPartyID(), part);			
		}
	}
	
	private Vehicle getVehicle(String id){
		if(vehicles.containsKey(id))
			return vehicles.get(id);
		return null;
	}
	
	private Party getParty(String id){
		if(parties.containsKey(id))
			return parties.get(id);
		return null;
	}
	
	private void removeVehicleFromMaps(Vehicle vec){
		removeVehicleFromMapItem(vehicleGreenItems, vec);
		removeVehicleFromMapItem(vehicleRedItems, vec);
	}
	
	private void removeVehicleFromMapItem(MapItems items, Vehicle vec){
		if(items.containsOverlay(vec.getVehicleID())){
	    	mapOverlays.remove(items);
	    	items.removeOverlay(vec.getVehicleID());
	    	mapOverlays.add(items); 
	    	mapView.invalidate();
    	}
		
	}
	
	private void insertOnMap(Vehicle vec){
		GeoPoint p = new GeoPoint(vec.getPosition().getLatitude(), vec.getPosition().getLongitude());
		MapItems items;
		
		removeVehicleFromMaps(vec);
		
		//if(vehicleItems.containsOverlay(vec.getVehicleID()))
    		//vehicleItems.removeOverlay(vec.getVehicleID());
		
		if(vec.getNparties()>0)
			items = vehicleRedItems;
		else
			items = vehicleGreenItems;
		
		items.addOverlay(new MapOverlayItem(vec.getVehicleID(), p, "Vehicle " + vec.getVehicleID(), "Welcome to iTaxi services!"));
    	//mapOverlays.remove(vehicleItems);
    	mapOverlays.add(items); 
    	mapView.invalidate();
        
	}
	
	private void addStations(){
		GeoPoint p = new GeoPoint(38742170,-9134210);
		MapItems items = new MapItems(Elements.STATIONS,getResources().getDrawable(R.drawable.gas), this);
		
		items.addOverlay(new MapOverlayItem("Gas station", p, "Gas Station ", "Welcome to iTaxi services!"));

    	mapOverlays.add(items); 
    	mapView.invalidate();
		
	}
	
	private void removeFromMap(Vehicle vec){
		//if(vehicleItems.containsOverlay(vec.getVehicleID()))
		
		removeVehicleFromMaps(vec);
		
	    /*mapOverlays.remove(vehicleItems);
	    vehicleItems.removeOverlay(vec.getVehicleID());
	    mapOverlays.add(vehicleItems); 
    	mapView.invalidate();*/
	}
	
	private void removePartyFromMapItems(Party party){
		removePartyFromMapItem(party,partyHappyItems);
		removePartyFromMapItem(party,partyImpacientItems);
		removePartyFromMapItem(party,partyAngryItems);
    }
    
    private void removePartyFromMapItem(Party party, MapItems items){  
    	if(items.containsOverlay(party.getPartyID())){
	    	mapOverlays.remove(items);
	    	items.removeOverlay(party.getPartyID());
	    	mapOverlays.add(items); 
	    	mapView.invalidate();
    	}
    }
	
	private void insertOnMap(Party part){
		GeoPoint p = new GeoPoint(part.getPosition().getLatitude(), part.getPosition().getLongitude());
		
		removePartyFromMapItems(part);
		
		/*if(partyItems.containsOverlay(part.getPartyID()))
    		partyItems.removeOverlay(part.getPartyID());*/
		
		CustomerState state = part.customerState();
		MapItems items = null;
		
		switch(state){
		case HAPPY:
			items = partyHappyItems;
			Log.d("Update", "insertOnMap HAPPY");
			break;
		case INIT:
			items = partyHappyItems;
			Log.d("Update", "insertOnMap HAPPY");
			break;
		case IMPACIENT:
			items = partyImpacientItems;
			Log.d("Update", "insertOnMap IMPACIENT");
			break;
		case ANGRY:
			items = partyAngryItems;
			Log.d("Update", "insertOnMap ANGRY");
			break;
		default:
			Log.d("Monitor", "Party with no state!");	
		}
		
		items.addOverlay(new MapOverlayItem(part.getPartyID(), p, "Party " + part.getPartyID(), "Welcome to iTaxi services!"));
		
    	mapOverlays.remove(items);
    	mapOverlays.add(items); 
    	mapView.invalidate();
        
	}
	
	private void removeFromMap(Party par){
		/*if(partyItems.containsOverlay(par.getPartyID()))
    		partyItems.removeOverlay(par.getPartyID());*/

    	/*mapOverlays.remove(partyItems);
    	partyItems.removeOverlay(par.getPartyID());
    	mapOverlays.add(partyItems); 
    	mapView.invalidate();*/
		
		removePartyFromMapItems(par);
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
    	//if(vehicleItems.containsOverlay(vehicle.getVehicleID()))
    	
    	MapItems items;
		
		removeVehicleFromMaps(vehicle);
		
		//if(vehicleItems.containsOverlay(vec.getVehicleID()))
    		//vehicleItems.removeOverlay(vec.getVehicleID());
		
		if(vehicle.getNparties() > 0)
			items = vehicleRedItems;
		else
			items = vehicleGreenItems;
    	
    	
		items.addOverlay(new MapOverlayItem(vehicle.getVehicleID(), gp, "", ""));
    	mapOverlays.add(items); 
    	mapView.invalidate();
    }
    
    private void updatePartyPosition(Party party) {
    	GeoPoint gp = new GeoPoint(party.getPosition().getLatitude(), party.getPosition().getLongitude());
    	Log.d("Update", "updatePartyPosition " + party.customerState());
    	/*
    	//if(partyItems.containsOverlay(party.getPartyID()))
    		partyItems.removeOverlay(party.getPartyID());
    		
		partyItems.addOverlay(new MapOverlayItem(party.getPartyID(), gp, "", ""));
    	mapOverlays.remove(partyItems);
    	mapOverlays.add(partyItems); 
    	mapView.invalidate();*/
    	
    	removePartyFromMapItems(party);
    	
    	CustomerState state = party.customerState();
		MapItems items = null;
		
		switch(state){
		case HAPPY:
			items = partyHappyItems;
			break;
		case IMPACIENT:
			items = partyImpacientItems;
			break;
		case ANGRY:
			items = partyAngryItems;
			break;
		case INIT:
			items = partyHappyItems;
			Log.d("Update", "insertOnMap HAPPY");
			break;
		default:
			Log.d("Monitor", "Party with no state!");	
		}
    	
    	items.addOverlay(new MapOverlayItem(party.getPartyID(), gp, "", ""));
    	mapOverlays.add(items); 
    	mapView.invalidate();
    }
    
           
	//Handles incoming messages
	private void handleMessage(Message message) {   //, Integer port) {
		String ID;
		switch (message.getType()) {
			case UPDATEVEHICLE:
				Vehicle vec = new Gson().fromJson(message.getContent(), Vehicle.class);
				Log.d("Monitor", "RECEIVED UPDATEVEHICLE:"+vec.getVehicleID()+" "+vec.get_position().getLatitude()+":"+vec.getPosition().getLongitude());
				addVehicle(vec);
				updateVehiclePosition(vec);
				//if(roamingVehicles.contains(vec.getVehicleID()))
					checkPositions(vec);
				//Log.d("Monitor", "UPDATED");
				break;
			
			case UPDATEPARTY:
				Party part = new Gson().fromJson(message.getContent(), Party.class);
				Log.d("Monitor", "RECEIVED UPDATEPARTY:" + part.getPartyID() + " " + part.customerState());
				
				addParty(part);
				/*if(!partiesSocks.containsKey(part.getPartyID())) 
					partiesSocks.put(part.getPartyID(),port);*/
				updatePartyPosition(part);
				//Log.d("Monitor", "UPDATED");
				break;
				 
			case REMOVE_VEHICLE:
				ID = message.getContent();
				Log.d("Monitor", "RECEIVED REMOVE VEHICLE:" + ID);
				Vehicle v = vehicles.get(ID);
				if(v!=null) {
					removeFromMap(v);
					vehicles.remove(ID);
				}
				break;
			case REMOVE_PARTY:
				ID = message.getContent();
				Log.d("Monitor", "RECEIVED REMOVE PARTY:" + ID);
				Party p = parties.get(ID);
				if(p!=null){
					removeFromMap(p);
					parties.remove(ID);
					waitingParties.remove(ID);
					//partiesSocks.remove(ID);
				}
				break;
			case PARTY_WAITING:
				ID = message.getContent();
				Log.d("Monitor", "RECEIVED PARTY_WAITING:" + ID);
				waitingParties.add(ID);
				break;
			case TAXI_ROAMING:
				Log.d("Monitor", "RECEIVED TAXY_ROAMING:" + message.getContent());
				roamingVehicles.add(message.getContent());
				break;
			default:
				Log.d("Monitor", "RECEIVED DEFAULT");
				break;
		}
	}
	
	private void checkPositions(Vehicle v) {
		for(String partyID : waitingParties) {
			Party p = parties.get(partyID);
			Log.d("Monitor", "waiting party:"+partyID + " "+ v.getPosition().distanceTo(p.getPosition()));
			if(v.getPosition().distanceTo(p.getPosition()) < NEARBY_DISTANCE) {
				Message message = new Message(MessageType.TAXI_ROAMING);
				Log.d("Monitor","A mandar TAXI:" + v.get_agentID() + " para:" + p.getPartyID() );
				//TODO Json bug ip
				message.setContent(gson.toJson(new ComponentIdentifier(v.get_agentID().getName(), null, v.get_agentID().getResolvers()) ,ComponentIdentifier.class));
				sendMessage("192.168.1.77", getPort(p.getPartyID()), message); //10.0.2.2
			}
		}
	}
	
	private static int getPort(String id){
		String name;
		int i_port=0;
		int nid = 0;
		if(id != null && id.length() > 0){
			for(int i=0; i < id.length(); i++)
				if(id.charAt(i) >= '0' && id.charAt(i) <= '9'){
					name = id.substring(0, i);
					if(name.equals("Customer") || name.startsWith("Customer_manual")) i_port=CUSTOMER_PORTS;
					else if(name.equals("Taxi") || name.startsWith("Taxi_manual")) i_port=TAXI_PORTS;
		
					nid = new Integer(id.substring(i));
					System.out.println("NAME:"+name + "PORT:"+ (i_port+nid));
					return i_port+nid ;
				}
		}
		return -1;
	}
	
	//Send message to client
	public synchronized void sendMessage(String ip, int port, Message message) {
		try {
			Socket socket = new Socket(ip, port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String msg = gson.toJson(message);
			writer.write(msg + "\n");
			writer.flush();
			Log.d("Monitor","Send Message to Party: " + msg);
			writer.close();
			socket.close();
		} catch (UnknownHostException e) {
			Log.d("Monitor",e.getMessage() + " " + ip + " " + port);
		} catch (IOException e) {
			Log.d("Monitor",e.getMessage() + " " + ip + " " + port);
		}
	}
	
	
	//Receives messages from server in the background
	private class ServerConnectionTask extends AsyncTask<Void, Object, Void> {
		
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
		protected void onProgressUpdate(Object... messages) {
			handleMessage((Message)messages[0]);    //(Integer)messages[0]);   //, );
		}
	}
	
	@Override
    protected boolean isRouteDisplayed() {
        return false;
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
		numberPassengers.setText("Passengers Transporting: " + vehicle.getNparties());
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

package smartfleet.vehicle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;

import org.apache.commons.net.telnet.TelnetClient;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class VehicleUtil {

	private static int EMULATORPORT;
	private static String EMULATORIP = "10.0.2.2";
	private static int INITIALPORT = 8000;

	//Choose port for accepting connections
	protected static ServerSocket choosePort() {
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
	}
	
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
			throw new IOException("Port already assigned");
		
	}
	
	//Apply geo fix command
	protected static void changeVehicleGPS(GeoPoint gp) {
		try {
			// Initiate a telnet connection to the emulator
			TelnetClient tc = new TelnetClient();
			tc.connect(EMULATORIP, EMULATORPORT);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(tc.getOutputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(tc.getInputStream()));
			
			// Remove first line of the emulator console 
			reader.readLine();
				reader.readLine();
			
			// Apply redir command
			writer.write("geo fix " + gp.getLongitudeE6()/1E6 + " " + gp.getLatitudeE6()/1E6 + "\n");
			writer.flush();
			reader.readLine();
			tc.disconnect();
		} catch (IOException e) {
			Log.d("Vehicle", e.getMessage());
		}
	}
	
	// Get/Set of the emulator port
	public static int getEmulatorPort() {
		return EMULATORPORT;
	}
	public static void setEmulatorPort(int eMULATORPORT) {
		EMULATORPORT = eMULATORPORT;
	}
}

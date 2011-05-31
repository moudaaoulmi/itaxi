package itaxi.communications;

import itaxi.messages.message.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

public class AcceptConnection extends Thread {
	
	//Socket for accepting new connections
	private ServerSocket serverSocket;
	
	//Control thread execution
	private boolean stop;
	
	//Serializes and deserializes json objects
	private Gson gson;
	
	public MessageHandler handler;
	
	public Object caller;
	

	//Start thread execution
	public AcceptConnection(int serverPort, Object obj,MessageHandler hand) {
		try {
			handler = hand;
			caller = obj;
			serverSocket = new ServerSocket(serverPort);
			gson = new Gson();
			stop = false;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void run() {
		
		//Accept new connections
		while(!stop) {
			try {
				Socket socket = serverSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				// Read incoming messages
				Message msg = gson.fromJson(reader.readLine(), Message.class);
				System.out.println("Received Message: " + gson.toJson(msg));
				handleMessage(writer, msg);
				reader.close();
				socket.close(); 	
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	//Send message to client
	public synchronized void sendMessage(String ip, int port, Message message) {
		try {
			Socket socket = new Socket(ip, port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String msg = gson.toJson(message);
			writer.write(msg + "\n");
			writer.flush();
			System.out.println("Send Message: " + msg);
			writer.close();
			socket.close();
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage() + " " + ip + " " + port);
		} catch (IOException e) {
			System.err.println(e.getMessage() + " " + ip + " " + port);
		}
	}
	
	//Send message to client
	public synchronized void sendMessage(BufferedWriter writer, Message message) {
		try {
			String msg = gson.toJson(message);
			writer.write(msg + "\n");
			writer.flush();
			System.out.println("Send Message: " + msg);
			writer.close();
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	//Handle received message
	public void handleMessage(BufferedWriter writer, Message message) {
		//recebe o writer para o caso de se querer confirmar
		handler.handleMessage(caller, message);
		//handler.handleMessage(caller, writer, message);
	}

	//Stop thread execution
	public void stopThread(){
		try {
			stop = true;
			serverSocket.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}

package itaxi.communications.communicator;

import itaxi.communications.messages.Message;

import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

public class Communicator extends Thread {
	
	//Socket for accepting new connections
	private ServerSocket serverSocket;
	
	//Control thread execution
	private boolean stop;
	
	//Serializes and deserializes json objects
	private final Gson gson = new Gson();
	
	private MessageHandler handler;
	
	//IExternalAccess used to notify Jadex agents of a new message
	private IExternalAccess agent;
	
	//IExternalAccess used to notify Jadex agents of a new message
	public Communicator(int serverPort, IExternalAccess agent) {
		try {
			serverSocket = new ServerSocket(serverPort);
			stop = false;
			this.agent=agent;
		} catch (IOException e) {
			//System.err.println(e.getMessage());
			System.out.println("Communicator couldn't stablish connection");
		}
	}
	
	//MessageHandler for assynchronous communication
	public Communicator(int serverPort, MessageHandler hand) {
		try {
			handler = hand;
			serverSocket = new ServerSocket(serverPort);
			stop = false;
		} catch (IOException e) {
			//System.err.println(e.getMessage());
			System.out.println("Communicator couldn't stablish connection");
		}
	}
		
	public void run() {
		
		if(handler!=null)
			System.out.println(handler.toString() + " waiting for connections.");
		
		//Accept new connections
		while(!stop) {
			try {
				Socket socket = serverSocket.accept();
				
				System.out.println(handler.toString() + " accepted connection.");
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				if (handler != null) {
					// Read incoming messages
					final Message msg = gson.fromJson(reader.readLine(), Message.class);
					System.out.println("Received Message: " + gson.toJson(msg));
					
					if(agent!=null) {
						agent.scheduleStep(new IComponentStep()
						  {
						    public Object execute(IInternalAccess ia)
						    {
						      IBDIInternalAccess scope = (IBDIInternalAccess)ia;
						      
						      IInternalEvent event = scope.getEventbase().createInternalEvent("taxi_nearby");
						      
						      event.getParameter("content").setValue( (Object)msg );
						      scope.getEventbase().dispatchInternalEvent(event);

						      return null;
						    }
						  });
					}
					else handleMessage(writer, msg);
					
					reader.close();
				}
				else
					reader.close();
				
				socket.close(); 	
			} catch (IOException e) {
				//System.err.println(e.getMessage());
				System.out.println("Communicator Accept connections error!");
			}
		}
	}
	
	//Send message to client
	public static synchronized void sendMessage(String ip, int port, Message message) {
		try {
			Socket socket = new Socket(ip, port);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String msg = new Gson().toJson(message);
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
	public static synchronized void sendMessage(BufferedWriter writer, Message message) {
		try {
			String msg = new Gson().toJson(message);
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
		handler.handleMessage(message);
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

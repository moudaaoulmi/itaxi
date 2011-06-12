package itaxi.communications.communicator;

import itaxi.communications.messages.Message;

import java.io.BufferedWriter;

public interface MessageHandler {
	void handleMessage(Message message);
	
	void handleMessage(BufferedWriter writer, Message message);

	//returns the name of the handler
	String toString();
}

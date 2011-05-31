package itaxi.communications.communicator;

import itaxi.communications.message.Message;

import java.io.BufferedWriter;

public interface MessageHandler {
	void handleMessage(Object obj,Message message);
	
	void handleMessage(Object obj,BufferedWriter writer, Message message);
}

package itaxi.communications;

import java.io.BufferedWriter;

import itaxi.messages.message.Message;

public interface MessageHandler {
	void handleMessage(Object obj,Message message);
	
	void handleMessage(Object obj,BufferedWriter writer, Message message);
}

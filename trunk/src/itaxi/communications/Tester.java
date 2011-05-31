package itaxi.communications;

import itaxi.messages.message.Message;

public class Tester {

	public void handleMessage(Message message){
		System.out.println("Tester : message handler - " + message.getContent());
	}
}

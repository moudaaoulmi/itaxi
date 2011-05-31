package itaxi.jade;

import itaxi.communications.messages.Message;

public class Tester {

	public void handleMessage(Message message){
		System.out.println("Tester : message handler - " + message.getContent());
	}
}

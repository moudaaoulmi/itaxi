package itaxi.jade;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.handlers.Tester2Handler;
import itaxi.communications.messages.Message;

import com.google.gson.Gson;

public class Tester2 {
	
	private Communicator communicator;
	private Gson gson;
	
	public Tester2(){
		Tester2Handler handler = new Tester2Handler();
		communicator = new Communicator(8001, this, handler);
		communicator.start();
		
		//vamos enviar
		/*Message message = new Message(MessageType.UPDATEVEHICLE);
		message.setContent(gson.toJson(new Vehicle()));
		communicator.sendMessage("localhost", 6001, message);*/
	}

	public void handleMessage(Message message){
		//aqui trata-se da mensagem!
		System.out.println("Tester : message handler - " + message.getContent());
		switch(message.getType()){
		}
	}
	
	public static void main(String[] args) {
		Tester2 tester = new Tester2();
	}
}

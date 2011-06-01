package itaxi.jade;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.handlers.TesterHandler;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;

import com.google.gson.Gson;

public class Tester {
	
	private Communicator communicator;
	private Gson gson;
	
	public Tester(){
		TesterHandler handler = new TesterHandler();
		communicator = new Communicator(8000, this, handler);
		communicator.start();
		
		//vamos enviar
		Message message = new Message(MessageType.UPDATEVEHICLE);
		message.setContent("mensagem de teste");
		communicator.sendMessage("localhost", 8001, message);
	}

	public void handleMessage(Message message){
		//aqui trata-se da mensagem!
		System.out.println("Tester : message handler - " + message.getContent());
		switch(message.getType()){
		}
	}
	
	public static void main(String[] args) {
		Tester tester = new Tester();
	}
}

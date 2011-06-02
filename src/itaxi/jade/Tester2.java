package itaxi.jade;

import itaxi.communications.communicator.Communicator;
import itaxi.communications.handlers.Tester2Handler;
import itaxi.communications.messages.Message;
import itaxi.communications.messages.MessageType;
import itaxi.messages.coordinates.Coordinates;
import itaxi.messages.entities.Vehicle;

import com.google.gson.Gson;

public class Tester2 {
	
	private Communicator communicator;
	private Gson gson;
	
	public Tester2(){
		Tester2Handler handler = new Tester2Handler();
		communicator = new Communicator(8001, this, handler);
		communicator.start();
		
		gson = new Gson();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//vamos enviar
		Message message = new Message(MessageType.UPDATEVEHICLE);
		String newcontent = gson.toJson(new Vehicle("Carro do ze", 50, 
				new Coordinates((int) (38.737169*1E6),(int) (-9.302655*1E6)), 
				0, 0, 0, ""));
		message.setContent(newcontent);
		communicator.sendMessage("localhost", 8002, message);
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

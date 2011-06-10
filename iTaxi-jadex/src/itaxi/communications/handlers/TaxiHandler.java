package itaxi.communications.handlers;
/*
import java.io.BufferedWriter;

import itaxi.communications.communicator.MessageHandler;
import itaxi.communications.messages.Message;
import itaxi.jade.Taxi;


public class TaxiHandler implements MessageHandler{

	private String name;
	
	public TaxiHandler(String name){
		this.name = name;
	}
	
	@Override
	public void handleMessage(Object obj, Message message) {
		((Taxi) obj).handleMessage(message);
	}
	
	@Override
	public void handleMessage(Object obj, BufferedWriter writer, Message message) {
		//o writer é util para o caso de se querer invocar AcceptConnection.sendMessage(writer, newMessage)
		//é uma especie de ACK sem ter de reabrir o socket. Devia chamar-se o handleMessage com writer e message
		((Taxi) obj).handleMessage(message);
	}

	@Override
	public String toString(){
		return name;
	}


}*/

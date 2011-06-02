package itaxi.communications.handlers;

import itaxi.communications.communicator.MessageHandler;
import itaxi.communications.messages.Message;
import itaxi.jade.Tester2;

import java.io.BufferedWriter;

public class Tester2Handler implements MessageHandler{

	@Override
	public void handleMessage(Object obj, Message message) {
		((Tester2) obj).handleMessage(message);
	}

	public void handleMessage(Object obj,BufferedWriter writer, Message message){
		//o writer é util para o caso de se querer invocar AcceptConnection.sendMessage(writer, newMessage)
		//é uma especie de ACK sem ter de reabrir o socket. Devia chamar-se o handleMessage com writer e message
		((Tester2) obj).handleMessage(message);
	}
	
	public String toString(){
		return "tester2";
	}
}

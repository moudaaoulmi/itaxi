package test.agents;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.Map;

import com.google.gson.Gson;
import communications.messages.Message;

public class AddGermanWordPlanC1 extends Plan{

	private static final long serialVersionUID = 3842818932964307988L;

	
	/*
	 MENSAGEM A ENVIAR - 
	 {"type":"UPDATEVEHICLE",
	 "content":"conteudo cifrado"}
	 */
	
	/*
	 * {"type":"UPDATEVEHICLE","content":"{\"_gasLevel\":100.0,\"_vehicleID\":\"taxi1\",\"_position\":{\"latitude\":39000000,\"longitude\":39000000},\"parked\":0,\"infoAge\":0,\"parties\":{},\"destinations\":[]}"}
	 * 
	 */
	
	@Override
	public void body() {
		
		Gson gson = new Gson();
		IMessageEvent me = (IMessageEvent) getReason();
		String jsonString = (String) me.getParameter(SFipa.CONTENT).getValue();
		
		Message message = gson.fromJson(jsonString, Message.class);
		
		Bananas bananas = gson.fromJson(message.getContent(), Bananas.class);
		
		String eword = bananas.getEword();
		String gword = bananas.getWord();
		
		Map<String,String> words = (Map<String,String>)getBeliefbase().getBelief("egwords").getFact();
		
		if(!words.containsKey(eword))
			words.put(eword, gword);
		
	}

}

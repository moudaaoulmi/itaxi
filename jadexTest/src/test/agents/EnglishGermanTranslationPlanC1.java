package test.agents;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import communications.messages.Message;
import communications.messages.MessageType;

//PASSIVE SERVICE PLANS WITH PLANS PRIORITY-----------------------------------
//Tudo o que é static é utilizado para correr o metodo da precondicao
//------------------------------------------------------------------------
public class EnglishGermanTranslationPlanC1 extends Plan {
    // Plan attributes.

	private static final long serialVersionUID = 4090366771177725507L;

	protected static Map dictionary;
	public static Map getDictionary()
	{
	  if(dictionary==null)
	  {
	    dictionary = new HashMap<String,String>();
	    dictionary.put("milk", "Milch");
	    dictionary.put("cow", "Kuh");
	    dictionary.put("cat", "Katze");
	    dictionary.put("cona", "pila");
	  }
	  return dictionary;
	}
	
	
	public EnglishGermanTranslationPlanC1() {
        // Initialization code.
		System.out.println("Created " + this);
    }

	public void body() {
		// Plan code.
		
		Gson gson = new Gson();

		Message newMessage = new Message(MessageType.TRANSLATE);
		newMessage.setContent(gson.toJson(new Bananas("cona","cona")));
		System.out.println(gson.toJson(newMessage));
		
		IMessageEvent me = (IMessageEvent) getReason();
		String jsonString = (String) me.getParameter(SFipa.CONTENT).getValue();
		
		Message message = gson.fromJson(jsonString, Message.class);
		
		Bananas bananas = gson.fromJson(message.getContent(), Bananas.class);
		
		String eword = bananas.getEword();
		String gword = bananas.getWord();
		
		Map<String,String> words = (Map<String,String>)getBeliefbase().getBelief("egwords").getFact();

		 if(words.containsKey(eword)) 
			 System.out.println("Translating: " + eword + " - "
				+ words.get(eword));
    }
}
package test.agents;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.TreeMap;

//SERVICE PLANS---------------------------------------------------
//esta sempre a correr e tem uma queue de mensagens definida no xml
public class EnglishGermanTranslationPlanB1 extends Plan {
    // Plan attributes.

	private static final long serialVersionUID = 4090366771177725507L;

	private TreeMap<String,String> words;
	
	public EnglishGermanTranslationPlanB1() {
        // Initialization code.
		System.out.println("Created " + this);
		words = new TreeMap<String,String>();
		words.put("cona", "pila");
    }

    public void body() {
        // Plan code.
    	while(true){
    		IMessageEvent me = waitForMessageEvent("request_translation");
    		if(me != null){
	    		String eword = (String) me.getParameter(SFipa.CONTENT).getValue();
	    		if(words.containsKey(eword))
	    			System.out.println("Translating: "+eword+" - "+words.get(eword));
	    		else
	    			System.out.println("Sorry word is not in database: "+eword);
    		}
    	}
    }
}
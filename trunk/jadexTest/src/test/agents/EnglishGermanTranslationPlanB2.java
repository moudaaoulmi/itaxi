package test.agents;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.util.TreeMap;

//PASSIVE SERVICE PLANS---------------------------------------------------
//e gerado um novo plano sempre que e activado o trigger definido no xml
//nao faz queue de mensagens e usa o getReason
//------------------------------------------------------------------------
public class EnglishGermanTranslationPlanB2 extends Plan {
    // Plan attributes.

	private static final long serialVersionUID = 4090366771177725507L;

	private TreeMap<String,String> words;
	
	public EnglishGermanTranslationPlanB2() {
        // Initialization code.
		System.out.println("Created " + this);
		words = new TreeMap<String,String>();
		words.put("cona", "pila");
    }

    public void body() {
        // Plan code.
    	
    		IMessageEvent me = (IMessageEvent) getReason();
    		if(me != null){
	    		String eword = (String) me.getParameter(SFipa.CONTENT).getValue();
	    		if(words.containsKey(eword))
	    			System.out.println("Translating: "+eword+" - "+words.get(eword));
	    		else
	    			System.out.println("Sorry word is not in database: "+eword);
    		}
    }
}
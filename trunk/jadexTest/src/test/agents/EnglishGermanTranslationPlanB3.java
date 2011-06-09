package test.agents;

import jadex.bdi.runtime.Plan;

import java.util.TreeMap;

//PASSIVE SERVICE PLANS WITH PARAMETERS-----------------------------------
//o plano pode receber ou devolver paramentros definidos no xml
//neste caso em vez de acedermos aqui ao conteudo da mensagem, este e
//entregue como parametro de entrada do plano (ver xml)
//------------------------------------------------------------------------
public class EnglishGermanTranslationPlanB3 extends Plan {
    // Plan attributes.

	private static final long serialVersionUID = 4090366771177725507L;

	private TreeMap<String,String> words;
	
	public EnglishGermanTranslationPlanB3() {
        // Initialization code.
		System.out.println("Created " + this);
		words = new TreeMap<String,String>();
		words.put("cona", "pila");
    }

    public void body() {
        // Plan code.
    	
    		String eword = (String)getParameter("eword").getValue();
    		//IMessageEvent me = (IMessageEvent) getReason();
    		//if(me != null){
	    		//String eword = (String) me.getParameter(SFipa.CONTENT).getValue();
	    		if(words.containsKey(eword))
	    			System.out.println("Translating: "+eword+" - "+words.get(eword));
	    		else
	    			System.out.println("Sorry word is not in database: "+eword);
    		//}
    }
}
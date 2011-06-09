package test.agents;

import jadex.bdi.runtime.Plan;

import java.util.HashMap;

//PASSIVE SERVICE PLANS WITH PLANS PRIORITY-----------------------------------
//Tudo o que é static é utilizado para correr o metodo da precondicao
//------------------------------------------------------------------------
public class EnglishGermanTranslationPlanB4 extends Plan {
    // Plan attributes.

	private static final long serialVersionUID = 4090366771177725507L;

	static HashMap<String, String> wordtable;
	
	static {
		  wordtable = new HashMap<String,String>();
		  wordtable.put("coffee", "Kaffee");
		  wordtable.put("milk", "Milch");
		  wordtable.put("cow", "Kuh");
		  wordtable.put("cat", "Katze");
		  wordtable.put("cona", "pila");
	}
	
	//invocado como precondicao de planos no xml
	public static boolean containsWord(String name) {
		  System.out.println("checking if contains shittty name: " + name);
		  return wordtable.containsKey(name);
	}
	
	public EnglishGermanTranslationPlanB4() {
        // Initialization code.
		System.out.println("Created " + this);
    }

	public void body() {
		// Plan code.

		/*IMessageEvent me = (IMessageEvent) getReason();
		String eword = (String) me.getParameter(SFipa.CONTENT).getValue();
		// if(wordtable.containsKey(eword)) -------> aqui ja vai saber q contem!!
		System.out.println("Translating: " + eword + " - "
				+ wordtable.get(eword));
    */}
}
package jadex.bdi.tutorial;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *  Seach a translation of a word online.
 */
public class SearchTranslationOnlineB4 extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public SearchTranslationOnlineB4()
	{
//		getLogger().info("Created: "+this);
		System.out.println("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  Execute the plan.
	 */
	public void body()
	{
		String eword = (String)((IMessageEvent)getReason()).getParameter(SFipa.CONTENT).getValue();
		try
		{
			//URL dict = new URL("http://dict.leo.org/?search="+eword);
			URL dict = new URL("http://wolfram.schneider.org/dict/dict.cgi?query="+eword);
			System.out.println("Following translations were found online at: "+dict);
			BufferedReader in = new BufferedReader(new InputStreamReader(dict.openStream()));
			String inline;
			while((inline = in.readLine())!=null)
			{
				if(inline.indexOf("<td>")!=-1 && inline.indexOf(eword)!=-1)
				{
					try
					{
						int start = inline.indexOf("<td>")+4;
						int end = inline.indexOf("</td", start);
						String worda = inline.substring(start, end);
						start = inline.indexOf("<td", start);
						start = inline.indexOf(">", start);
						end = inline.indexOf("</td", start);
						String wordb = inline.substring(start, end==-1? inline.length()-1: end);
						wordb = wordb.replaceAll("<b>", "");
						wordb = wordb.replaceAll("</b>", "");
						System.out.println(worda+" - "+wordb);
					}
					catch(Exception e)
					{
						System.out.println(inline);
					}
				}
			}
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

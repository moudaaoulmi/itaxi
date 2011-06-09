package test.agents;

import jadex.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class SearchTranslationOnlineB4 extends Plan{

	private static final long serialVersionUID = -2303713623579121629L;
	
	@Override
	public void body() {
		IMessageEvent me = (IMessageEvent) getReason();
		String eword = (String) me.getParameter(SFipa.CONTENT).getValue();
		System.out.println("Online search : " + eword);
		try {

			URL dict = new URL(
					"http://wolfram.schneider.org/dict/dict.cgi?query=" + eword);
			BufferedReader in = new BufferedReader(new InputStreamReader(dict
					.openStream()));
			String inline;
			while ((inline = in.readLine()) != null) {
				if (inline.indexOf("<td>") != -1 && inline.indexOf(eword) != -1) {
					int start = inline.indexOf("<td>") + 4;
					int end = inline.indexOf("</td", start);
					String worda = inline.substring(start, end);
					start = inline.indexOf("<td", start);
					start = inline.indexOf(">", start);
					end = inline.indexOf("</td", start);
					String wordb = inline.substring(start, end == -1 ? inline
							.length() - 1 : end);
					wordb = wordb.replaceAll("<b>", "");
					wordb = wordb.replaceAll("</b>", "");
					System.out.println(worda + " - " + wordb);
				}
			}
			in.close();
		} catch (Exception e) {
			System.out.println("deu merda!");
		}
	}

}

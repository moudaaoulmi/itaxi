package itaxi.jadex.centralserver;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.service.SServiceProvider;

public class GetTaxisPlan extends Plan {

	private static final long serialVersionUID = -2396854901610843639L;
	
	//beliefs-------------------------------------
	//protected static Map<String, Vehicle> availableTaxis = new TreeMap<String, Vehicle>();
	
	/*public static Map<String, Vehicle> getAvailableTaxis() {
		return availableTaxis;
	}*/

	@Override
	public void body() {
		System.out.println("GetTaxisPlan body");
		IComponentIdentifier[] ta = getTaxis();
		for(IComponentIdentifier ici : ta ) {
			System.out.println("Found taxi:" + ici);
		}
	}

	public IComponentIdentifier[] getTaxis() {
		IComponentIdentifier[] ta=null;

		while (ta == null) {
			IDF dfservice = (IDF) SServiceProvider.getService(
					getScope().getServiceProvider(), IDF.class).get(this);

			// Create a service description to search for.
			IDFServiceDescription sd = dfservice.createDFServiceDescription(
					"service_taxi", "taxi", "IST");
			IDFComponentDescription ad = dfservice
					.createDFComponentDescription(null, sd);
  
			// Use a subgoal to search for a translation agent
			IGoal ft = createGoal("df_search");
			ft.getParameter("description").setValue(ad);

			dispatchSubgoalAndWait(ft);
			// Object result = ft.getResult();
			IDFComponentDescription[] result = (IDFComponentDescription[]) ft
					.getParameterSet("result").getValues();

			if (result.length > 0) {
				ta = new IComponentIdentifier[result.length];
				for(int i=0; i<result.length ; i++) {
					ta[i]=result[i].getName();
				}
				return ta;
			} else {
				System.out.println("No taxi agent found.");
				waitFor(5000);
			}
		}
		return ta;
	}
}

package itaxi.jadex.customer;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.service.SServiceProvider;

public class GetCentralServerPlan extends Plan {

	private static final long serialVersionUID = 9130108129275164217L;

	public void body() {
		System.out.println("GetCentralServerPlan body");
		IComponentIdentifier[] ta = getCentralServer();
		//		for(IComponentIdentifier ici : ta ) {
		//			System.out.println("Found taxi:" + ici);
		//		}

		getParameter("centralServer").setValue(ta);
	}

	public IComponentIdentifier[] getCentralServer() {
		IComponentIdentifier[] ta=null;

		IDF dfservice = (IDF) SServiceProvider.getService(
				getScope().getServiceProvider(), IDF.class).get(this);

		// Create a service description to search for.
		IDFServiceDescription sd = dfservice.createDFServiceDescription(
				"service_taxiCentral", "taxiCentral", "IST");
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
			System.out.println("No centralServer agent found.");
			//			waitFor(5000);
		}

		return ta;
	}
}

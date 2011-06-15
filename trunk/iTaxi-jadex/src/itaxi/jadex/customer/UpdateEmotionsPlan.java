package itaxi.jadex.customer;

import java.util.Date;

import jadex.bdi.runtime.Plan;


public class UpdateEmotionsPlan extends Plan{

	private static final long serialVersionUID = -157047142458487574L;
	
	public static Date getDate()
	{
	  return new Date();
	}

	@Override
	public void body() {
		CustomerState state = (CustomerState) getBeliefbase().getBelief("emotional_state").getFact();
		switch(state){
		case HAPPY:
			state = CustomerState.IMPACIENT;
			System.out.println("Customer state = IMPACIENT");
			break;
		case IMPACIENT:
			state = CustomerState.ANGRY;
			System.out.println("Customer state = ANGRY");
			break;
		case ANGRY:
			break;
		}
		
		getBeliefbase().getBelief("emotional_state").setFact(state);
	}

}

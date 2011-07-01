package itaxi.jadex.customer;

public enum CustomerState {
	INIT,
	HAPPY,
	IMPACIENT,
	ANGRY,
	UNDEFINED;
	
	public static CustomerState convert( int i ) {
		for ( CustomerState current : values() ) {
		if ( current.ordinal() == i ) {
		return current;
		}
		}
		return UNDEFINED;
		}
}
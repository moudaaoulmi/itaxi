package itaxi.messages.exceptions;

public class PartySizeException extends Exception{


	private static final long serialVersionUID = 7202132446371684432L;

	public PartySizeException(){
		super("Invalid party size");
	}
}

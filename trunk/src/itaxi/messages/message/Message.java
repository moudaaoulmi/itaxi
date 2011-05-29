package itaxi.messages.message;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.net.util.Base64;

public class Message {
	
	private MessageType type;
	private String content;
		
	public Message() {
		
	}

	public void Cipher(){

		// Instantiate the cipher

		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] raw = Base64.decodeBase64("HKAep+DhbKb/RJlhxnB22g==");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(raw, "AES"));

			byte[] encrypted =
				cipher.doFinal(content.getBytes());
			
			//System.out.println("encrypted content: " + Base64.encodeBase64String(encrypted));
			
			this.content = Base64.encodeBase64String(encrypted);
		
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void Decipher(){
		Cipher cipher;
		try {
			
			byte[] raw = Base64.decodeBase64("HKAep+DhbKb/RJlhxnB22g==");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(raw, "AES"));
			
			byte[] original = cipher.doFinal(Base64.decodeBase64(this.content));
			String originalString = new String(original);
			
			//System.out.println("Original string: " + originalString);
			
			this.content = originalString;
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Message(MessageType type) {
		this.type = type;
		this.content = "";
		Cipher();
	}
	
	public Message(MessageType type, String content) {
		this.type = type;
		this.content = content;
		Cipher();
	}
	
	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	public String getContent() {
		String result = "";
		Decipher();
		result = content;
		Cipher();
		return result;
	}
	
	public void setContent(String content) {
		this.content = content;
		Cipher();
	}
	
}

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class HandshakeMessage extends Message{
	
	private String header;
	private byte[] zeroField;
	private int peerID;
	
	public HandshakeMessage(int peerID)
	{
		this.peerID=peerID;
		header="HELLO";
		zeroField = new byte[23];
		Arrays.fill(zeroField,(byte)0);
	}
	public HandshakeMessage(String handshakeString)
	{
		
	}
	
	@Override
	public String toString()
	{
		String zeroFieldString = null;
		try {
			zeroFieldString = new String(zeroField, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String string = header+zeroFieldString+Integer.toString(peerID);
		return string;
	}
}

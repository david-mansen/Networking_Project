import java.util.Arrays;


public class HandshakeMessage extends Message{
	
	private String header;
	private Byte[] zeroField;
	private int peerID;
	
	public HandshakeMessage(int peerID)
	{
		this.peerID=peerID;
		header="HELLO";
		zeroField = new Byte[23];
		Arrays.fill(zeroField,(byte)0);
	}
	public HandshakeMessage(String handshakeString)
	{
		
	}
	
	@Override
	public String toString()
	{
		String string = header+zeroField.toString()+Integer.toString(peerID);
		return string;
	}
}

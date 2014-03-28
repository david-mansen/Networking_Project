import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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


	@Override
	public byte[] toByteArray()
	{
		byte[] headerBytes = header.getBytes();
		
		
		ByteBuffer peerIDBuffer = ByteBuffer.allocate(4);
		peerIDBuffer.putInt(peerID);
		byte[] peerIDBytes = peerIDBuffer.array();
		
		byte[] bytes = new byte[32];
		for(int i = 0; i<5; i++)
		{
			bytes[i] = headerBytes[i];
		}
		for(int i = 5; i<28; i++ )
		{
			bytes[i] = 0x00; //zero field bytes
		}
		for(int i = 28; i<32; i++)
		{
			bytes[i] = peerIDBytes[i-28];
		}
		return bytes;
	}
	
}

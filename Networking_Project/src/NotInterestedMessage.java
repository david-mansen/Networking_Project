import java.nio.ByteBuffer;



public class NotInterestedMessage extends Message{
	
	private int length;
	private byte type;
	//no payload
	
	public NotInterestedMessage()
	{
		length = 1;
		type = 0x03;
		//no payload
	}
	
	
	@Override
	public String toString()
	{
		String string = Integer.toString(length) + Integer.toString(type);
		return string;
	}


	@Override
	public byte[] toByteArray() {
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
		lengthBuffer.putInt(length);
		byte[] lengthBytes = lengthBuffer.array();
		
		byte[] bytes = new byte[5];
		for(int i = 0; i<4; i++)
		{
			bytes[i] = lengthBytes[i];
		}
		bytes[4] = type;
		return bytes;
	}
}

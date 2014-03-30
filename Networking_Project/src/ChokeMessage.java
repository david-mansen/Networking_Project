import java.nio.ByteBuffer;



public class ChokeMessage extends Message{
	
	private int length;
	private byte type;
	//no payload
	
	public ChokeMessage()
	{
		length = 1;
		type = 0x00;
		//no payload
	}
	
	public byte[] toByteArray()
	{
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}

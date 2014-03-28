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
	
//	int aLen = A.length;
//	   int bLen = B.length;
//	   Foo[] C= new Foo[aLen+bLen];
//	   System.arraycopy(A, 0, C, 0, aLen);
//	   System.arraycopy(B, 0, C, aLen, bLen);
//	   return C;
	
	
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

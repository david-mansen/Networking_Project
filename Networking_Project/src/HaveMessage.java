import java.nio.ByteBuffer;


public class HaveMessage extends Message{

	int length;
	byte type;
	int havePieceIndex;
	
	public HaveMessage(int havePieceIndex)
	{
		length = 5;
		type = 0x04;
		this.havePieceIndex = havePieceIndex;
	}
	
	public HaveMessage(byte[] payload)
	{
		havePieceIndex = (payload[3] & 0xFF) | (payload[2] & 0xFF) << 8 | 
				(payload[1] & 0xFF) << 16 | (payload[0] & 0xFF) << 24;
	}
	
	public byte[] toByteArray()
	{
		//length
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
		lengthBuffer.putInt(length);
		byte[] lengthBytes = lengthBuffer.array();
		//
		ByteBuffer havePieceIndexBuffer = ByteBuffer.allocate(4);
		havePieceIndexBuffer.putInt(havePieceIndex);
		byte[] havePieceIndexBytes = havePieceIndexBuffer.array();
		//pieceIndex bytes
		
		//compose byte array
		byte[] bytes = new byte[9];
		for(int i = 0; i<=3; i++)
		{
			bytes[i] = lengthBytes[i];
		}
		bytes[4] = type;
		for(int i=5; i<bytes.length; i++ )
		{
			bytes[i] = havePieceIndexBytes[i-5];
		}
		return bytes;
	}
	
	public int getHavePieceIndex()
	{
		return havePieceIndex;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	

}

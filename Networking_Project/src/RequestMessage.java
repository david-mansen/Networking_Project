import java.nio.ByteBuffer;


public class RequestMessage extends Message{

	int length;
	byte type;
	int requestedPiece;
	
	public RequestMessage(int requestedPiece)
	{
		length = 5;
		type = 0x06;
		this.requestedPiece = requestedPiece;
	}
	
	public RequestMessage(byte[] payload)
	{
		requestedPiece = (payload[3] & 0xFF) | (payload[2] & 0xFF) << 8 | 
				(payload[1] & 0xFF) << 16 | (payload[0] & 0xFF) << 24;
	}
	
	public byte[] toByteArray()
	{
		//length
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
		lengthBuffer.putInt(length);
		byte[] lengthBytes = lengthBuffer.array();
		//
		ByteBuffer requestedPieceBuffer = ByteBuffer.allocate(4);
		requestedPieceBuffer.putInt(requestedPiece);
		byte[] requestedPieceBytes = requestedPieceBuffer.array();
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
			bytes[i] = requestedPieceBytes[i-5];
		}
		return bytes;
	}
	
	public int getRequestedPiece()
	{
		return requestedPiece;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}



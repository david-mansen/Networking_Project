import java.nio.ByteBuffer;


public class PieceMessage extends Message
{
	int length;
	byte type;
	int pieceIndex;
	byte[] piece;
	
	
	public PieceMessage(int pieceIndex, byte[] piece)
	{
		this.pieceIndex = pieceIndex;
		this.piece = piece;
		type = 0x07;
		length = 1 + 4 + piece.length;
	}
	
	public PieceMessage(byte[] payload)
	{
		type = 0x07;
		length = 1 + payload.length;
		piece = new byte[length - 5]; // subtract 1 byte for type, 4 bytes for index
		
		pieceIndex = (payload[3] & 0xFF) | (payload[2] & 0xFF) << 8 | 
				(payload[1] & 0xFF) << 16 | (payload[0] & 0xFF) << 24;
		
		for(int i=4; i<payload.length; i++)
		{
			piece[i-4] = payload[i]; 
		}
		
	}
	
	@Override
	public byte[] toByteArray() {
		//length bytes
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
		lengthBuffer.putInt(length);
		byte[] lengthBytes = lengthBuffer.array();
		
		//pieceIndexBytes
		ByteBuffer pieceIndexBuffer = ByteBuffer.allocate(4);
		pieceIndexBuffer.putInt(length);
		byte[] pieceIndexBytes = pieceIndexBuffer.array();
		
		byte[] bytes = new byte[4 + length];
		
		
		for(int i=0; i<=3; i++)
		{
			bytes[i] = lengthBytes[i];
		}
		bytes[4] = type;
		
		for(int i=5; i<pieceIndexBytes.length; i++)
		{
			bytes[i] = pieceIndexBytes[i-5];
		}
		
		for(int i=9; i<bytes.length; i++)
		{
			bytes[i] = piece[i-9];
		}
		
		return bytes;
	}
	
	public int getPieceIndex()
	{
		return pieceIndex;
	}
	
	public byte[] getPiece()
	{
		return piece;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}



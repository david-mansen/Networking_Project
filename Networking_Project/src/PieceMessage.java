
public class PieceMessage extends Message{
	int pieceRecieved;
	byte[] pieceNUM;
	byte[] data; 
	
	public PieceMessage(byte[] Payload, int senderID,Peer peer){
		for(int i=0;i<4;i++)
			pieceNUM[i] = Payload[i];
		//pieceNUM = java.nio.ByteBuffer.wrap(pieceNUM).getInt();
		for(int i=0;i<Payload.length-4;i++)
			data[i] = Payload[i+4];
		//peer.pieceRecieved(pieceRecieved, data, senderID);
	}


	public String toString(){
		return null;
	}
}

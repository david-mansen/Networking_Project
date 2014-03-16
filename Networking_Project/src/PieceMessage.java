
public class PieceMessage extends Message{
	int senderID;
	int pieceRecieved;
	byte[] pieceNUMB;
	byte[] data; 
	
	public PieceMessage(byte[] Payload, int senderID,Peer peer){
		for(int i=0;i<4;i++)
			pieceNUMB[i] = Payload[i];
		pieceNUM = java.nio.ByteBuffer.wrap(pieceNUMB).getInt();
		this.senderID = senderID;
		for(int i=0;i<Payload.length-4;i++)
			data[i] = Payload[i+4];
		peer.pieceRecieved(pieceRecieved, data, senderID);
	}


	public String toString(){
		
	}
}

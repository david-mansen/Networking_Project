
public class RequestMessage extends Message{

	int senderID;
	int pieceRequested;
	
	public RequestMessage(byte[] Payload, int senderID,Peer peer){
		pieceRequested = java.nio.ByteBuffer.wrap(Payload).getInt();
		this.senderID = senderID;
		peer.requestRecieved(pieceRequested, senderID);
	}


	public String toString(){
		
		
	}
}

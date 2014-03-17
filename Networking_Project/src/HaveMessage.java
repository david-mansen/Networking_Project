
public class HaveMessage extends Message{

	int senderID;
	int pieceNum;
	
	public HaveMessage(byte[] Payload, int senderID,Peer peer){
		int pieceNum = java.nio.ByteBuffer.wrap(Payload).getInt();
		this.senderID = senderID;
		//peer.updatePeerBitfiled(pieceNum, peerID) //Update bitfields for SenderID
	}


	public String toString(){
		
		return null;
	}

}

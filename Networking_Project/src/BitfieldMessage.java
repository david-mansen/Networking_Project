
public class BitfieldMessage extends Message{
	int senderID;
	boolean[] bitfiled;
	
//	public BitfieldMessage(byte[] Payload, int senderID,Peer peer){
//		bitfield = new boolean[Payload.length*8]//convert payload to boolean array somehow
//		for(i=0;i<;i++>){
//			bitfield[i] = ((Payload[i] & 0x01) != 0);
//			bitfield[i+1] = ((Payload[i] & 0x02) != 0);
//			bitfield[i+2] = ((Payload[i] & 0x04) != 0);
//			bitfield[i+3] = ((Payload[i] & 0x08) != 0);
//			bitfield[i+4] = ((Payload[i] & 0x10) != 0);
//			bitfield[i+5] = ((Payload[i] & 0x20) != 0);
//			bitfield[i+6] = ((Payload[i] & 0x40) != 0);
//			bitfield[i+7] = ((Payload[i] & 0x80) != 0);
//			
//		}
//				
//		this.senderID = senderID;
//		peer.assignBitfiled(pieceNum, peerID) //Assign bitfield for SenderID
//	}


	public String toString(){
		
		return null;
	}
	
	
}


public class BitfieldMessage extends Message{
	int senderID;
	boolean[] bitfield;
	
	public BitfieldMessage(byte[] Payload, int senderID,Peer peer){
		bitfield = new boolean[Payload.length*8];
		int j=0;
		for(int i=0;i<Payload.length;i++){
			bitfield[j] = ((Payload[i] & 0x01) != 0);
			bitfield[j+1] = ((Payload[i] & 0x02) != 0);
			bitfield[j+2] = ((Payload[i] & 0x04) != 0);
			bitfield[j+3] = ((Payload[i] & 0x08) != 0);
			bitfield[j+4] = ((Payload[i] & 0x10) != 0);
			bitfield[j+5] = ((Payload[i] & 0x20) != 0);
			bitfield[j+6] = ((Payload[i] & 0x40) != 0);
			bitfield[j+7] = ((Payload[i] & 0x80) != 0);
			j = j+8;
		}
				
		this.senderID = senderID;
		//peer.assignSwarmPeerBitfieldd(bitfield, this.senderID);
	}


	public String toString(){
		
		return null;
	}


	@Override
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

import java.net.Socket;
import java.util.ArrayList;


public class SwarmPeer {
	//parameters from PeerInfo.cfg
		private int peerID;				//e.g. 1001
		private String hostName; 		//e.g. sun114-11.cise.ufl.edu
		private int portNum;			//e.g. 6008 
		private boolean hasEntireFile;	//indicated by last digit in peerinfo.cfg
		
		//other fields 
		private boolean[] bitfield;
		
		public SwarmPeer(int peerID, String hostName, int portNum, boolean hasEntireFile, int numPieces) 
		{
			this.peerID = peerID;
			this.hostName = hostName;
			this.portNum = portNum;
			this.hasEntireFile = hasEntireFile;
			
			
			initializeBitfield(numPieces);
			
			System.out.println("Peer " + peerID +"created");
		}
		
		private void initializeBitfield(int numPieces)
		{
			
			bitfield = new boolean[numPieces];
			
			if(hasEntireFile == true)
			{
				for(int i=0; i<numPieces; i++)
				{
					bitfield[i]=true;
				}
			}
			else
			{
				for(int i=0; i<numPieces; i++)
				{
					bitfield[i]=false;
				}
			}
			
		}
		
		public int getPeerID() {
			return peerID;
		}

		public String getHostName() {
			return hostName;
		}

		public int getPortNum() {
			return portNum;
		}
		
}

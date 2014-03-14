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
			bitfield = new boolean[numPieces];
			System.out.println("Peer " + peerID +"created");
		}
		
}

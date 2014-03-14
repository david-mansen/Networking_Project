import java.net.Socket;
import java.util.ArrayList;


public class SwarmPeer {
	//parameters from PeerInfo.cfg
		private int peerID;				//e.g. 1001
		private String address; 		//e.g. sun114-11.cise.ufl.edu
		private int portNum;			//e.g. 6008 
		private boolean hasEntireFile;	//indicated by last digit in peerinfo.cfg
		
		//other fields 
		
		public SwarmPeer(int peerID, String address, int portNum, boolean hasEntireFile) 
		{
			this.peerID = peerID;
			this.address = address;
			this.portNum = portNum;
			this.hasEntireFile = hasEntireFile;
			
			System.out.println("Peer " + peerID +"created");
		}
		
}

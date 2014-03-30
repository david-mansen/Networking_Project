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
		
		private boolean isConnected = false;  //true when this swarm peer has an input connection to receive
		private boolean optimisticallyUnchoked = false; //true if optimistically preferred peer
		private boolean isChoked = true;     //true if choked
		private boolean interested = false;  //true if interested, false if Not interested
		
		private int bytesDownloadedLastInterval = 0;  //
		private int unchokingInterval;
		
		public SwarmPeer(int peerID, String hostName, int portNum, boolean hasEntireFile, int numPieces, int unchokingInterval) 
		{
			this.peerID = peerID;
			this.hostName = hostName;
			this.portNum = portNum;
			this.hasEntireFile = hasEntireFile;
			this.unchokingInterval = unchokingInterval;
			
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
		
		public synchronized void updateBitfield(int pieceNumber)
		{
			bitfield[pieceNumber] = true;
		}
		
		public synchronized void setBitfield(boolean[] newBitfield)
		{
			for(int i=0; i<this.bitfield.length; i++)
			{
				this.bitfield[i] = newBitfield[i];
			}
		}
		
		public synchronized boolean[] getBitfield()
		{
			return bitfield;
		}
		
		public synchronized void setInterested(boolean interested)
		{
			this.interested = interested;
		}
		
		public synchronized void setIsChoked(boolean isChoked)
		{
			this.isChoked = isChoked;
		}
		
		public synchronized void setOptimisticallyUnchoked()
		{
			isChoked = false;
			optimisticallyUnchoked = true;
		}
		
		public synchronized void setIsConnected(boolean isConnected)
		{
			this.isConnected = isConnected;
		}
		
		public synchronized boolean getIsConnected()
		{
			return isConnected;
		}
		
		public synchronized boolean getIsChoked()
		{
			return isChoked;
		}
		
		public synchronized boolean getOptimisticallyUnchoked()
		{
			return optimisticallyUnchoked;
		}
		
		public synchronized boolean getInterested()
		{
			return interested;
		}
		
		public synchronized float getDownloadRate()
		{
			return (float)bytesDownloadedLastInterval/(float)unchokingInterval;
		}
}

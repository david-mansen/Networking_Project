import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Peer {
	//parameters from PeerInfo.cfg
	private int peerID;				//e.g. 1001
	private String hostName; 		//e.g. sun114-11.cise.ufl.edu
	private int portNum;			//e.g. 6008 
	private boolean hasEntireFile;	//indicated by last digit in peerinfo.cfg
	
	//parameters from Common.cfg
	private int numPreferredNeighbors; 
	private int unchokingInterval;
	private int optimisticUnchokingInterval;
	private String fileName;		//name of file to be distributed
	private int fileSize;			//size of total file distributed
	private int pieceSize;			// size in bytes of each piece
	
	//other fields
	private int numPieces;
	private boolean[] bitfield;		//1 or 0 indicates the peer has the piece or not
	
	private Socket uploadSocket;
	
	private ArrayList<SwarmPeer> otherPeers;
	private ArrayList<SwarmPeer> preferredPeers;
	
	
	public Peer(int peerID) 
	{
		this.peerID = peerID;
		otherPeers = new ArrayList<SwarmPeer>(5);
		
		initialize();
		bitfield = new boolean[numPieces];
		
	}
	
	public void initialize()
	{
		//read Common.cfg
		Scanner scanner;
			try
			{
				scanner = new Scanner(new File("Common.cfg"));
				
			}
			catch(FileNotFoundException error)
			{
				System.out.println("File not found");
				throw new RuntimeException("Common.cfg not found", error);
			}
			scanner.next();
			numPreferredNeighbors = scanner.nextInt();
			scanner.next();
			unchokingInterval = scanner.nextInt();
			scanner.next();
			optimisticUnchokingInterval = scanner.nextInt();
			scanner.next();
			fileName = scanner.next();
			scanner.next();
			fileSize = scanner.nextInt();
			scanner.next();
			pieceSize = scanner.nextInt();
		scanner.close();

		//end reading common.cfg
		
		numPieces = fileSize/pieceSize; //calculate numPieces, need for bitfields
		
		//read PeerInfo.cfg
		try
		{
			scanner = new Scanner(new File("PeerInfo.cfg"));
			
		}
		catch(FileNotFoundException error)
		{
			throw new RuntimeException("PeerInfo.cfg not found", error);
		}
		while(scanner.hasNext()){
			int peerID = scanner.nextInt();
			String hostName = scanner.next();
			int portNum = scanner.nextInt();
			int hasEntireFileInteger= scanner.nextInt();
			boolean hasEntireFile;
			if(hasEntireFileInteger == 1){
				hasEntireFile = true;
			}
			else{
				hasEntireFile = false;
			}
			
			if(this.peerID == peerID)
			{
				//initialize fields of this instance
				this.hostName = hostName;
				this.portNum = portNum;
				this.hasEntireFile = hasEntireFile;
			}
			else
			{
				//create a peer with these parameters 
				SwarmPeer swarmPeer = new SwarmPeer(peerID,hostName,portNum,hasEntireFile,numPieces);
				otherPeers.add(swarmPeer);
			}
		}
		scanner.close();
	}
}

import java.net.Socket;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Peer {
	//parameters from PeerInfo.cfg
	private int peerID;				//e.g. 1001
	private String address; 		//e.g. sun114-11.cise.ufl.edu
	private int portNum;			//e.g. 6008 
	private boolean hasEntireFile;	//indicated by last digit in peerinfo
	
	//parameters from Common.cfg
	private int numPreferredNeighbors; 
	private int unchokingInterval;
	private int optimisticUnchokingInterval;
	private String fileName;		//name of file to be distributed
	private int fileSize;			//size of total file distributed
	private int pieceSize;			// size in bytes of each piece
	
	//other fields
	Socket uploadSocket;
	
	ArrayList<Integer> otherPeers;
	ArrayList<Integer> preferredPeers;
	
	
	public Peer(int peerID) 
	{
		otherPeers = new ArrayList<Integer>(5);
		
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
		
		//read PeerInfo.cfg
//		try
//		{
//			scanner = new Scanner(new File("PeerInfo.cfg"));
//			
//		}
//		catch(FileNotFoundException error)
//		{
//			throw new RuntimeException("Common.cfg not found", error);
//		}
//		while(scanner.hasNext()){
//			
//			
//		}
//		
//		scanner.next();
//		numPreferredNeighbors = scanner.nextInt();
//		scanner.next();
//		unchokingInterval = scanner.nextInt();
//		scanner.next();
//		optimisticUnchokingInterval = scanner.nextInt();
//		scanner.next();
//		fileName = scanner.next();
//		scanner.next();
//		fileSize = scanner.nextInt();
//		scanner.next();
//		pieceSize = scanner.nextInt();
//		scanner.close();
//		//end reading peerinfo.cfg
	}
}

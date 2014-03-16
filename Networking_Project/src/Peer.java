import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.net.*;


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
	
	
	private ArrayList<SwarmPeer> otherPeers;
	private ArrayList<SwarmPeer> preferredPeers;
	
	private OutputConnection outputConnection1;
	private InputConnection inputConnection1;
	
	private boolean handshakeReceived;
	private boolean connectionEstablished;
	
	public Peer(int peerID) 
	{
		this.peerID = peerID;
		otherPeers = new ArrayList<SwarmPeer>(5);
		
		handshakeReceived=false;
		connectionEstablished = false;
		
		readConfigFiles();  //this reads the settings for this peer as well as initialize the other peers
		
		initializeBitfield();
		initializeDirectory();
		
		initializeConnections();
		int i=0;
		while(i!=1){
			
			
		}
	}
	
	private void initializeConnections()
	{
		for(SwarmPeer connectPeer : otherPeers)
		{
			if(connectPeer.getPeerID() < 1003)
			{
				if(connectPeer.getPeerID() > peerID)
				{
					//create a thread that creates a ServerSocket
					writeToLogFile("\ncreating input connection");

					inputConnection1 = new InputConnection(this);
					writeToLogFile("input connection created");
					while(handshakeReceived == false)
					{
						System.out.println(String.valueOf(handshakeReceived));
						//do nothing while waiting for handshake
					}
					writeToLogFile("handshake received, copying to output connection");

					outputConnection1 = new OutputConnection(this,inputConnection1.getSocket());
					writeToLogFile("output connection created");

					while(connectionEstablished == false)
					{
						System.out.println("waiting for output connection to establish");
					}
					writeToLogFile("output connection established");

				}
				else
				{
					//create a thread that creates a Socket to establish connection with ServerSocket
					writeToLogFile("\nPreparing to establish outputconnection");
					outputConnection1 = new OutputConnection(this,connectPeer);
					writeToLogFile("Output connection created");
					while(connectionEstablished == false)
					{
						//wait for connection
					}
					writeToLogFile("connection successfully established,  copying to input connection");
	
					inputConnection1 = new InputConnection(this,outputConnection1.getSocket());
					writeToLogFile("input connection created successfully");
	
					while(handshakeReceived == false)
					{
						//do nothing while waiting for handshake
					}
				}
			
			}
		}
	}
	
	public void writeToLogFile(String log)
	{
		System.out.println("Writing to log...");
		
		File tempLogFile = null;
		tempLogFile = new File("log_peer_"+peerID+".log");
		if(!tempLogFile.exists())
		{
			System.out.println("File not found.  Creating new log file");
			try
			{
				tempLogFile.createNewFile();
			}
			catch(IOException error)
			{
				throw new RuntimeException("Error creating log file");
			}
		}
		try
		{
			FileWriter fileWriter = new FileWriter(tempLogFile,true); //true means append to file
			BufferedWriter logWriter = new BufferedWriter(fileWriter);
			logWriter.write(log);
			logWriter.newLine();
			logWriter.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error writing to log file");
		}
	}
	
	public void readConfigFiles()
	{
		//read Common.cfg
		Scanner scanner = null;
			try
			{
				scanner = new Scanner(new File("Common.cfg"));
			}
			catch(FileNotFoundException error)
			{
				System.out.println("File Common.cfg not found.  Exiting...");
				System.exit(0);
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
			System.out.println("File PeerInfo.cfg not found.  Exiting...");
			System.exit(0);
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
	
	private void initializeDirectory()
	{
		//this function has 2 uses.  For peers with entire file, it makes sure the file exists in the proper location
		//For peers without the entire file, it checks/creates the proper directories
		if(hasEntireFile) 
		{
			System.out.println("checking for file");
			File entireFile = null;
			
			//entireFile = new File("../peer_"+peerID+"/"+fileName);
			entireFile = new File("peer_"+peerID+"/"+fileName);

			if(entireFile.exists() && !entireFile.isDirectory())
			{ 
				System.out.println("file exists");

			}	
			else
			{
				System.out.println("Error: Peer should have entire file, but cannot locate the file");
				System.exit(0);
			}
		}
		else
		{
			System.out.println("doesnt have entire file");
			File fileDirectory = null;
			fileDirectory = new File("peer_"+peerID+"/");
			if(fileDirectory.exists() && fileDirectory.isDirectory())
			{
				System.out.println("Directory found");
			}
			else
			{
				System.out.println("Directory Not Found. Making directory");
				fileDirectory.mkdirs();
			}
		}
		
	}
	
	private void initializeBitfield()
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

	public String getFileName() {
		return fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getPieceSize() {
		return pieceSize;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public boolean[] getBitfield() {
		return bitfield;
	}
	
	public void setHandshakeReceived(boolean handshakeReceived)
	{
		this.handshakeReceived=handshakeReceived;
	}
	
	public void setConnectionEstablished(boolean connectionEstablished)
	{
		this.connectionEstablished=connectionEstablished;
	}
}

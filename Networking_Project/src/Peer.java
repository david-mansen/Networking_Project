import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
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
	
	private OutputConnection outputConnection1;
	private InputConnection inputConnection1;
	
	private ArrayList<OutputConnection> outputConnections;
	private ArrayList<InputConnection> inputConnections;
	
	private int numPeersDownloading;
	
	private ServerSocket serverSocket;
	
	public Peer(int peerID) 
	{
		numPeersDownloading = 2;
		//timers
		long currentUnchokingTimer = 0;
		long currentOptimisticUnchokingTimer = 0;
		long currentExitTimer = 0;
		long lastTimeMilliseconds = 0;
		long thisTimeMilliseconds = 0;
		long deltaTimeMilliseconds = 0;
		
		Timer exitTimer = new Timer();
		exitTimer.scheduleAtFixedRate(new TimerTask()
			{
				@Override
				public void run()
				{
					endProgram();
				}
			}, 15*1000, 15*1000);
		
		
		// end timers
		this.peerID = peerID;
		otherPeers = new ArrayList<SwarmPeer>(5);
		
		try
		{
			serverSocket = new ServerSocket(portNum);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		outputConnections = new ArrayList<OutputConnection>();
		inputConnections = new ArrayList<InputConnection>();

		readConfigFiles();  //this reads the settings for this peer as well as initialize the other peers
		
		initializeBitfield();
		initializeDirectory();
		
		initializeConnections();
		int i=0;
		while(i!=1){
			
			thisTimeMilliseconds = System.currentTimeMillis();
			deltaTimeMilliseconds = thisTimeMilliseconds - lastTimeMilliseconds;
			lastTimeMilliseconds = thisTimeMilliseconds;
			
			currentUnchokingTimer = currentUnchokingTimer + deltaTimeMilliseconds;
			currentOptimisticUnchokingTimer = currentOptimisticUnchokingTimer + deltaTimeMilliseconds;
			currentExitTimer = currentExitTimer + deltaTimeMilliseconds;
			
			if((currentUnchokingTimer/1000) >= unchokingInterval )
			{
				currentUnchokingTimer = 0;
				System.out.println("5 seconds passed");
			}
			if((currentOptimisticUnchokingTimer/1000) >= optimisticUnchokingInterval)
			{
				currentOptimisticUnchokingTimer = 0;
				System.out.println("15 seconds passed");
			}
			//if((currentExitTimer/1000) >= 30)
			//{
		//		outputConnection1.interrupt();
			//	inputConnection1.interrupt();
			//}

			
//			if(numPeersDownloading <= 0)
//			{
//				System.out.println("Exiting");
//				if(outputConnection1 != null)
//				outputConnection1.interrupt();
//				if(inputConnection1 != null)
//				inputConnection1.interrupt();
//				System.exit(0);
//			}
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
					InputConnection inputConnection = new InputConnection(this);
					addInputConnection(inputConnection);
				}
				else
				{
					OutputConnection outputConnection = new OutputConnection(this, connectPeer);
					addOutputConnection(outputConnection);
				}
			}
		}
	}
	
	
	public synchronized void writeToLogFile(String log)
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
		
		numPieces = (int)Math.ceil((float)fileSize/(float)pieceSize); //calculate numPieces, need for bitfields
		
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
			if(hasEntireFileInteger == 1)
			{
				hasEntireFile = true;
			}
			else
			{
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
	

	public synchronized int getPeerID() {
		return peerID;
	}

	public synchronized String getHostName() {
		return hostName;
	}

	public synchronized int getPortNum() {
		return portNum;
	}

	public synchronized String getFileName() {
		return fileName;
	}

	public synchronized int getFileSize() {
		return fileSize;
	}

	public synchronized int getPieceSize() {
		return pieceSize;
	}

	public synchronized int getNumPieces() {
		return numPieces;
	}

	public synchronized boolean[] getBitfield() {
		return bitfield;
	}
	
	public synchronized void addInputConnection(InputConnection inputConnection)
	{
		inputConnections.add(inputConnection);
	}
	
	public synchronized void addOutputConnection(OutputConnection outputConnection)
	{
		outputConnections.add(outputConnection);
	}
	
	public synchronized OutputConnection getOutputConnection()
	{
		return outputConnection1;
	}
	
	public synchronized InputConnection getInputConnection()
	{
		return inputConnection1;
	}
	
	public synchronized void decrementNumPeersDownloading()
	{
		numPeersDownloading--;
	}
	
	public synchronized int getNumPeersDownloading()
	{
		return numPeersDownloading;
	}
	
	public void updateSwarmPeerBitfield(int pieceNumber, int SwarmPeerID){
		for(int i=0;i< otherPeers.size();i++){
			//if(SwarmPeerID == otherPeers.get(i).getPeerID())
			//otherPeers.get(i).updateBitfield(pieceNumber);
		}
	}
	
	public void assignSwarmPeerBitfieldd(boolean[] bitfield, int SwarmPeerID){
		for(int i=0;i< otherPeers.size();i++){
			//if(SwarmPeerID == otherPeers.get(i).getPeerID())
			//otherPeers.get(i).assignRecievedBitfield(bitfield);
		}
	}

	public void requestRecieved(int pieceRequested, int senderID){
		if(bitfield[pieceRequested] == true){
		//Send piece from method or return somewhere else to handle
		}
		else{
		//We don't have piece requested
		}
	}

	public void pieceRecieved(int pieceRecieved, byte[] data){
		bitfield[pieceRecieved] = true;
		//Store data somewhere specific to peer, directrory
	}
	
	public ArrayList<SwarmPeer> getOtherPeers()
	{
		return otherPeers;
	}
	
	public synchronized ServerSocket getServerSocket()
	{
		System.out.println("server socket");
		return serverSocket;
	}
	
	private void endProgram()
	{
		System.out.println("Exiting");
		for(OutputConnection outputConnection : outputConnections)
		{
			if(outputConnection != null) outputConnection.interrupt();
		}
		for(InputConnection inputConnection : inputConnections)
		{
			if(inputConnection != null) inputConnection.interrupt();
		}
		
		System.exit(0);
	}

}

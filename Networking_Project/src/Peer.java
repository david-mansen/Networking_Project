import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
	
	private ArrayList<OutputConnection> outputConnections;
	private ArrayList<InputConnection> inputConnections;
	
	private int numPeersDownloading;
	
	private ServerSocket serverSocket;
	
	private int forceExitTime = 20;
	
	public Peer(int peerID) 
	{
		numPeersDownloading = 2;
		
		this.peerID = peerID;
		otherPeers = new ArrayList<SwarmPeer>(5);
		
		outputConnections = new ArrayList<OutputConnection>();
		inputConnections = new ArrayList<InputConnection>();
		
		readConfigFiles();  //this reads the settings for this peer as well as initialize the other peers
		
		initServerSocket();
		
		initializeBitfield();
		initializeDirectory();
		
		if(hasEntireFile)
		{
			breakFileIntoPieces(); //only if peer has entire file
		}
		
		setTimers();
		
		initializeConnections();
		
		int i=0;
		while(i!=1){
			
		}
	}
	
	public synchronized void receiveBitfieldMessage(SwarmPeer senderPeer, BitfieldMessage message)
	{
		boolean hasDesiredPiece = false;
		
		senderPeer.setBitfield(message.getBitfield());
		for(int i=0; i<bitfield.length; i++)
		{
			//System.out.println("receiver: "+bitfield[i]+"sender: "+senderPeer.getBitfield()[i]);
			if((bitfield[i] == false) && (senderPeer.getBitfield()[i] == true))
			{
				hasDesiredPiece = true;
			}
		}
		for(OutputConnection outputConnection : outputConnections)
		{
			if(outputConnection.getReceiverPeer() == senderPeer)
			{
				if(hasDesiredPiece == true)
				{
					System.out.println("ADDING INTERESTED MESSAGE TO QUEUE");
					InterestedMessage interestedMessage = new InterestedMessage();
					outputConnection.addMessageToQueue(interestedMessage);
				}
				else
				{
					System.out.println("ADDING NOT_INTERESTED MESSAGE TO QUEUE");
					NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
					outputConnection.addMessageToQueue(notInterestedMessage);
				}
			}
		}
	}
	
	
	private void breakFileIntoPieces()
	{
		File completeFile = new File("peer_"+peerID+"/"+fileName);
		//System.out.println("File Size: "+completeFile.length());
		
		InputStream inputStream = null;
		try
		{
			inputStream = new BufferedInputStream(new FileInputStream(completeFile));
			byte[] pieceBytes = new byte[pieceSize];
			for(int i=0; i<numPieces; i++)
			{
				try
				{
					int bytesRead = inputStream.read(pieceBytes);
					if(bytesRead>0)
					{
						for(int j=bytesRead; j<pieceSize; j++)
						{
							pieceBytes[j] = 0x00;
						}
					}
					writePieceToFile(i, pieceBytes);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				//for(int j=0; j<pieceBytes.length; j++)
				//{
					//System.out.println(pieceBytes[j]);
				//}
			}
		}
		catch (FileNotFoundException error)
		{
			error.printStackTrace();
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void writePieceToFile(int pieceIndex, byte[] pieceBytes)
	{
		File pieceFile = null;
		pieceFile = new File("peer_"+peerID+"/"+"piece_"+pieceIndex+".dat");
		if(!pieceFile.exists())
		{
			System.out.println("Piece file not found, creating new piece file");
			try
			{
				pieceFile.createNewFile();
			}
			catch(IOException error)
			{
				throw new RuntimeException("Error creating piece");
			}
		}
		try
		{
			FileWriter fileWriter = new FileWriter(pieceFile,false); //true means append to file
			BufferedWriter pieceWriter = new BufferedWriter(fileWriter);
			for(int i=0; i<pieceBytes.length; i++)
			{
				pieceWriter.write(pieceBytes[i]);
			}
			pieceWriter.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error writing to piece file");
		}
	}
	
	public synchronized byte[] getPiece(int pieceIndex)
	{
		byte[] piece = new byte[pieceSize];
		
		File pieceFile = new File("peer_"+peerID+"/"+"piece_"+pieceIndex+".dat");
		System.out.println("File Size: "+pieceFile.length());
		
		InputStream inputStream = null;
		try
		{
			inputStream = new BufferedInputStream(new FileInputStream(pieceFile));
			try
			{
				int bytesRead = inputStream.read(piece);
				//System.out.println("Read "+bytesRead+" bytes from piece.");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			//for(int j=0; j<piece.length; j++)
			//{
			//	System.out.println(piece[j]);
			//}
		}
		catch (FileNotFoundException error)
		{
			error.printStackTrace();
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return piece;
	}
	
	private void mergePieces()
	{
		File mergedFile = null;
		mergedFile = new File("peer_"+peerID+"/"+"mergedFile.dat");
		if(!mergedFile.exists())
		{
			System.out.println("Piece file not found, creating new piece file");
			try
			{
				mergedFile.createNewFile();
			}
			catch(IOException error)
			{
				throw new RuntimeException("Error creating merged File");
			}
		}
		try
		{
			FileWriter fileWriter = new FileWriter(mergedFile,false); //true means append to file
			BufferedWriter mergedFileWriter = new BufferedWriter(fileWriter);
			for(int i=0; i<numPieces; i++)
			{
				byte[] pieceBytes = getPiece(i);
				for(int j=0; j<pieceBytes.length; j++)
				{
					mergedFileWriter.write(pieceBytes[j]);
				}
			}
			mergedFileWriter.close();
		}
		catch(IOException e)
		{
			throw new RuntimeException("Error writing to piece file");
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
	
	public synchronized OutputConnection getOutputConnection(SwarmPeer connectedPeer)
	{
		for(OutputConnection outputConnection : outputConnections)
		{
			if(outputConnection.getReceiverPeer() == connectedPeer)
			{
				return outputConnection;
			}
		}
		return null;
	}
	
	public synchronized InputConnection getInputConnection(SwarmPeer connectedPeer)
	{
		for(InputConnection inputConnection : inputConnections)
		{
			if(inputConnection.getSenderPeer() == connectedPeer)
			{
				return inputConnection;
			}
		}
		return null;
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
	
	public synchronized ArrayList<SwarmPeer> getOtherPeers()
	{
		return otherPeers;
	}
	
	public synchronized ServerSocket getServerSocket()
	{
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
		writeToLogFile(" ");
		System.exit(0);
	}
	
	private void setTimers()
	{
		//timers
				Timer exitTimer = new Timer();
				exitTimer.scheduleAtFixedRate(new TimerTask()
					{
						@Override
						public void run()
						{
							endProgram();
						}
					}, forceExitTime*1000, forceExitTime*1000);
				
				Timer unchokingTimer = new Timer();
				unchokingTimer.scheduleAtFixedRate(new TimerTask()
					{
						@Override
						public void run()
						{
							System.out.println("unchoking");
						}
					}, unchokingInterval*1000, unchokingInterval*1000);
				
				Timer optimisticUnchokingTimer = new Timer();
				optimisticUnchokingTimer.scheduleAtFixedRate(new TimerTask()
					{
						@Override
						public void run()
						{
							System.out.println("optimistic unchoking");
						}
					}, optimisticUnchokingInterval*1000, optimisticUnchokingInterval*1000);
				
				// end timers
	}

	private void initServerSocket()
	{
		try
		{
			serverSocket = new ServerSocket(portNum);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;


public class InputConnection extends Thread
{
	private ServerSocket serverSocket;
	private Socket inputSocket;
	private Peer peer;
	private SwarmPeer senderPeer;
	
	private boolean handshakeReceived;
	public boolean done;
	
	private int senderPeerID;
	
	public InputConnection(Peer peer)
	{
		done=false;
		this.peer=peer;
		serverSocket = null;
		senderPeer = null;
		handshakeReceived = false;
		this.start(); //start the thread
	}
	public InputConnection(Peer peer, Socket existingSocket, SwarmPeer senderPeer)
	{
		done=false;
		this.peer = peer;
		this.senderPeer = senderPeer;
		inputSocket = existingSocket;
		handshakeReceived = false;
		this.start(); //start the thread
	}
	
	public void run()
	{
		//peer.writeToLogFile("waiting for connection");
		waitForConnection(); //only if connection not already established
		//peer.writeToLogFile("established connection");
		int senderPeerID=waitForHandshakeMessage();//returns the peerID of connecting peer
		
		assignSenderPeer(senderPeerID);
		
		if(peer.getOutputConnection(senderPeer) == null)
		{
			peer.addOutputConnection(new OutputConnection(peer,inputSocket,senderPeer));
		}
		
		peer.decrementNumPeersDownloading();
		int i=0;
		while(i!=1)
		{
			
		}
	}
	
	public int waitForHandshakeMessage()
	{
		byte[] inputBytes = new byte[32];
		int numBytes;
		DataInputStream inputStream = null;
		try
		{
			inputStream = new DataInputStream(inputSocket.getInputStream());
		} 
		catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}
		
		int index = 0;
		while(index < 32)
		{
			try
			{
				byte tempByte = inputStream.readByte();
				inputBytes[index] = tempByte;
				index++;
				System.out.println(tempByte);
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		// get info from handshake message
		byte[] helloBytes = new byte[5];
		byte[] zeroFieldBytes = new byte [23];
		byte[] peerIDBytes = new byte[4];
		
		for(int i=0;i<=4;i++)
		{
			helloBytes[i] = inputBytes[i];
		}
		for(int i=5;i<=27;i++)
		{
			zeroFieldBytes[i-5] = inputBytes[i];
		}
		for(int i=28;i<=31;i++)
		{
			peerIDBytes[i-28] = inputBytes[i];
		}
		String stringHello = null;
		String stringZeroField = null;
		String stringPeerID = null;
		try
		{
			stringHello = new String(helloBytes,"UTF-8");
			stringZeroField = new String(zeroFieldBytes,"UTF-8");
			stringPeerID = new String(peerIDBytes,"UTF-8");
		}
		catch(UnsupportedEncodingException error)
		{
			error.printStackTrace();
		}
		int intPeerID = Integer.parseInt(stringPeerID);
		peer.writeToLogFile("["+(new Date().toString())+"]: Peer [peer_ID "+peer.getPeerID()+
				"] is connected from Peer [peer_ID "+intPeerID+"].");
		handshakeReceived = true;
		return intPeerID;
	}
	
	public void waitForMessage()
	{
		
		
	}
	
	public void waitForNormalMessage(){
		byte[] lengthT_P = new byte[4]; //bytes representing length of message
		DataInputStream inputStream = null;
		try
		{
			inputStream = new DataInputStream(inputSocket.getInputStream());
		} 
		catch (IOException exception)
		{
			throw new RuntimeException(exception);		
		}
		for(int i=0;i<lengthT_P.length;i++){
		//	lengthT_P[i] = inputStream.readByte(); 
		}
		
		int lengthMessage = java.nio.ByteBuffer.wrap(lengthT_P).getInt();
		byte[] MType = new byte[1];
		//MType[0] = inputStream.readByte();
	
		byte[] MPay;//Bytes representing payload length
		int TypeMessage = java.nio.ByteBuffer.wrap(MType).getInt();//integer value representing message
		switch(TypeMessage){
			case 0: 
				//Set choke
				break;
			case 1:
				//Set unchoke
				break;
			case 2: 
				//Set interested
				break;
			case 3:
				//Set Uninterested
				break;
			case 4:
				MPay= new byte[lengthMessage-1];
				for(int i=0;i<MPay.length;i++){
				//	MPay[i] = inputStream.readByte(); 
				}
				//HaveMessage HM = new HaveMessage(MPay, senderID, peer); //Need sender ID
				break;
			case 5: 
				MPay= new byte[lengthMessage-1];
				for(int i=0;i<MPay.length;i++){
				//	MPay[i] = inputStream.readByte(); 
				}
				//BitfieldMessage BM = new BitfiledMessage(MPay, senderID, peer);
				break;
			case 6:
				MPay= new byte[lengthMessage-1];
				for(int i=0;i<MPay.length;i++){
					//MPay[i] = inputStream.readByte(); 
				}
				//RequestMessage RM = new RequestMessage(MPay, senderID, peer);
				break;
			case 7:
				MPay= new byte[lengthMessage-1];
				for(int i=0;i<MPay.length;i++){
					//MPay[i] = inputStream.readByte(); 
				}
				//PieceMessage PM = new PieceMessage(MPay, peer);
				break;
		}
	}
	
	
	public void waitForConnection()
	{
		if(serverSocket == null)
		{
			serverSocket = peer.getServerSocket();
//			try
//			{
//				serverSocket = new ServerSocket(peer.getPortNum());
//			}
//			catch(UnknownHostException exception)
//			{
//				throw new RuntimeException(exception);
//			}
//			catch(IOException exception)
//			{
//				throw new RuntimeException(exception);
//			}
		}
		
		if(inputSocket == null)
		{
			while(inputSocket==null)
			{
				System.out.println("testing block");
				try
				{
					inputSocket = serverSocket.accept();
				}
				catch(UnknownHostException exception)
				{
					throw new RuntimeException(exception);
				}
				catch(IOException exception)
				{
					throw new RuntimeException(exception);
				}
			}
		}
	}

	
	
	private void assignSenderPeer(int senderPeerID)
	{
		for(SwarmPeer senderPeer : peer.getOtherPeers())
		{
			if(senderPeer.getPeerID() == senderPeerID)
			{
				this.senderPeer = senderPeer;
				return;
			}	
		}
		peer.writeToLogFile("should have assigned sender peer");
	}

	public synchronized SwarmPeer getSenderPeer()
	{
		return senderPeer;
	}
	
	public synchronized Socket getSocket()
	{
		return inputSocket;
	}
	
}

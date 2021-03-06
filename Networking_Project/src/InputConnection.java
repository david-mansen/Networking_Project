import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
		
		senderPeer.setIsConnected(true); //set flag that this swarmpeer has input/output connections
		
		int i=0;
		while(i!=1)
		{
			Message message = waitForMessage();
			System.out.println("INPUT CONNECTION RECEIVED MESSAGE");
			handleMessage(message);
		}
	}
	
	private void handleMessage(Message message)
	{
		if(message instanceof ChokeMessage)
		{
			peer.receiveChokeMessage(senderPeer);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] is choked by ["+senderPeer.getPeerID()+"].");
		}
		if(message instanceof UnchokeMessage)
		{
			peer.receiveUnchokeMessage(senderPeer);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] is unchoked by ["+senderPeer.getPeerID()+"].");
		}
		if(message instanceof InterestedMessage)
		{
			senderPeer.setInterested(true);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] received an 'interested' message from ["+senderPeer.getPeerID()+"].");
		}
		if(message instanceof NotInterestedMessage)
		{
			senderPeer.setInterested(false);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] received a 'not interested' message from ["+senderPeer.getPeerID()+"].");
		}
		if(message instanceof HaveMessage)
		{
			int havePieceIndex;
			havePieceIndex = ((HaveMessage) message).getHavePieceIndex();
			System.out.println("Have message piece index: "+havePieceIndex);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] received a 'have' message from ["+senderPeer.getPeerID()+"] for the piece "+havePieceIndex+".");
			peer.receiveHaveMessage(senderPeer, (HaveMessage)message);
		}
		if(message instanceof BitfieldMessage )
		{
			peer.receiveBitfieldMessage(senderPeer, (BitfieldMessage)message);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] received a bitfield message from ["+senderPeer.getPeerID()+"].");
		}
		if(message instanceof RequestMessage)
		{
			int requestedPiece;
			requestedPiece = ((RequestMessage) message).getRequestedPiece();
			peer.receiveRequestMessage(senderPeer, (RequestMessage)message);
			System.out.println("Request message requested piece: "+requestedPiece);
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] received a request message from ["+senderPeer.getPeerID()+"].");
		}
		if(message instanceof PieceMessage)
		{
			
			peer.receivePieceMessage(senderPeer, (PieceMessage)message);
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
		try
		{
			stringHello = new String(helloBytes,"UTF-8");
			stringZeroField = new String(zeroFieldBytes,"UTF-8");
		}
		catch(UnsupportedEncodingException error)
		{
			error.printStackTrace();
		}
		//converting peer id bytes
		int int_peerID = (peerIDBytes[3] & 0xFF) | ((peerIDBytes[2] & 0xFF) << 8) 
				| ((peerIDBytes[1] & 0xFF) << 16) | ((peerIDBytes[0] & 0xFF) << 24);
		//
		if(peer.getPeerID() < int_peerID) //connected from
		{
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] is connected from Peer ["+int_peerID+"].");
		}
		else //connected to
		{
			peer.writeToLogFile("["+(new Date().toString())+"]: Peer ["+peer.getPeerID()+
					"] makes a connection to Peer ["+int_peerID+"].");
		}
		
		handshakeReceived = true;
		return int_peerID;
	}
	
	public Message waitForMessage()
	{
		int length = 0;
		byte[] lengthBytes = new byte[4];
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
		//get length
			int lengthIndex = 0;
			while(lengthIndex < 4)
			{
				try
				{
					byte tempByte = inputStream.readByte();
					lengthBytes[lengthIndex] = tempByte;
					lengthIndex++;
				}
				catch(Exception e)
				{
					//do nothing
				}
			}
			
			length = (lengthBytes[3] & 0xFF) | ((lengthBytes[2] & 0xFF) << 8) 
					| ((lengthBytes[1] & 0xFF) << 16) | ((lengthBytes[0] & 0xFF) << 24);
		//end get length 
			
		//get type
		boolean typeRetrieved=false;
		byte typeByte = 0;
		while(typeRetrieved == false)
		{
			try
			{
				typeByte = inputStream.readByte();
				typeRetrieved = true;
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		int type = (typeByte & 0xFF);
		//end get type
		//get payload
		int payloadIndex = 0;
		byte[] payloadBytes = new byte[length-1];
		while(payloadIndex < length-1)
		{
			try
			{
				byte tempByte = inputStream.readByte();
				payloadBytes[payloadIndex] = tempByte;
				payloadIndex++;
			}
			catch(Exception e)
			{
				//do nothing
			}
		}
		
		// end payload
		switch(type)
		{
			case 0:
				ChokeMessage chokeMessage = new ChokeMessage();
				return chokeMessage;
				
			case 1:
				UnchokeMessage unchokeMessage = new UnchokeMessage();
				return unchokeMessage;
				
			case 2:
				InterestedMessage interestedMessage = new InterestedMessage();
				return interestedMessage;
				
			case 3:
				NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
				return notInterestedMessage;
				
			case 4:
				HaveMessage haveMessage = new HaveMessage(payloadBytes);
				return haveMessage;
				
			case 5:
				BitfieldMessage bitfieldMessage = new BitfieldMessage(payloadBytes);
				return bitfieldMessage;
				
			case 6:
				RequestMessage requestMessage = new RequestMessage(payloadBytes);
				return requestMessage;
				
			case 7:
				PieceMessage pieceMessage = new PieceMessage(payloadBytes);
				return pieceMessage;
				
			default:
				return null;
		}
	}
	
	
	public void waitForConnection()
	{
		if(serverSocket == null)
		{
			serverSocket = peer.getServerSocket();
		}
		
		if(inputSocket == null)
		{
			while(inputSocket==null)
			{
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

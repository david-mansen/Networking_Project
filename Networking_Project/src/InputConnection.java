import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class InputConnection extends Thread
{
	private ServerSocket serverSocket;
	private Socket inputSocket;
	private Peer peer;
	
	private boolean handshakeReceived;
	public boolean done;
	
	public InputConnection(Peer peer)
	{
		done=false;
		this.peer=peer;
		serverSocket = null;
		handshakeReceived = false;
		this.start(); //start the thread
	}
	public InputConnection(Peer peer, Socket existingSocket)
	{
		done=false;
		this.peer=peer;
		inputSocket = existingSocket;
		handshakeReceived = false;
		this.start(); //start the thread
	}
	
	public void run()
	{
		waitForConnection(); //only if connection not already established
		
		int peerID=waitForHandshakeMessage();//returns the peerID of connecting peer
		
		if(peer.getOutputConnection() == null)
		{
			peer.createOutputConnection(new OutputConnection(peer,inputSocket));
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
		do{
			try
			{
				numBytes = inputStream.read(inputBytes);
				System.out.println("num bytes read:" +numBytes);
			}
			catch(Exception exception)
			{
				throw new RuntimeException(exception);
			}
			
			System.out.println("waiting on handshake");
		}while(numBytes != 32);
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
		handshakeReceived = true;
		return intPeerID;
	}
	
//	public void waitForNormalMessage(){
//		byte[] lengthT_P = new byte[4]; //Length of bytes representing length of message
//		DataInputStream inputStream = null;
//		try
//		{
//			inputStream = new DataInputStream(inputSocket.getInputStream());
//		} 
//		catch (IOException exception)
//		{
//			throw new RuntimeException(exception);		
//		}
//		for(int i=0;i<lengthT_P.length;i++){
//			lengthT_P[i] = inputStream.readByte(); 
//		}
//		
//		int lengthMessage = java.nio.ByteBuffer.wrap(lengthT_P).getInt();
//		byte[] MType = new byte[1];
//		MType[0] = inputStream.readByte();
//	
//		byte[] MPay;
//		int TypeMessage = java.nio.ByteBuffer.wrap(MType).getInt();
//		switch(TypeMessage){
//			case 0: 
//				//Set choke
//				break;
//			case 1:
//				//Set unchoke
//				break;
//			case 2: 
//				//Set interested
//				break;
//			case 3:
//				//Set Uninterested
//				break;
//			case 4:
//				MPay= new byte[lengthMessage-1];
//				for(int i=0;i<MPay.length;i++){
//					MPay[i] = inputStream.readByte(); 
//				}
//				HaveMessage HM = new HaveMessage(MPay, senderID, peer); //Need sender ID
//				break;
//			case 5: 
//				MPay= new byte[lengthMessage-1];
//				for(int i=0;i<<MPay.length;i++){
//					MPay[i] = inputStream.readByte(); 
//				}
//				BitfieldMessage BM = new BitfiledMessage(MPay, senderID, peer);
//				break;
//			case 6:
//				MPay= new byte[lengthMessage-1];
//				for(int i=0;i<<MPay.length;i++){
//					MPay[i] = inputStream.readByte(); 
//				}
//				RequestMessage RM = new RequestMessage(MPay);
//				break;
//			case 7:
//				MPay= new byte[lengthMessage-1];
//				for(int i=0;i<<MPay.length;i++){
//					MPay[i] = inputStream.readByte(); 
//				}
//				PieceMessage PM = new PieceMessage(MPay);
//				break;
//			case default :
//				break;
//		}
		
	//}
	
	public void waitForConnection()
	{
		if(inputSocket == null)
		{
			try
			{
				serverSocket = new ServerSocket(peer.getPortNum());
			}
			catch(UnknownHostException exception)
			{
				throw new RuntimeException(exception);
			}
			catch(IOException exception)
			{
				throw new RuntimeException(exception);
			}
			
			while(inputSocket==null)
			{
				try
				{
					System.out.println("connection established on input");
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

	public Socket getSocket()
	{
		return inputSocket;
	}
	

}

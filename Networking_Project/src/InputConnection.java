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
	
	
	
	public InputConnection(Peer peer)
	{
		this.peer=peer;
		serverSocket = null;
		this.start(); //start the thread
	}
	public InputConnection(Peer peer, Socket existingSocket)
	{
		this.peer=peer;
		inputSocket = existingSocket;
		this.start(); //start the thread
	}
	
	public void run()
	{
		System.out.println("input connection running");
		peer.writeToLogFile("Waiting for connection");
		
		waitForConnection(); //only if connection not already established
		
		peer.writeToLogFile("Waiting for handshake message");
		
		int peerID=waitForHandshakeMessage();//returns the peerID of connecting peer
		
		peer.writeToLogFile("Input connection received Client with peer id: "+peerID);
		
		peer.createOutputConnection(new OutputConnection(peer,inputSocket));
		
		peer.writeToLogFile("output connection created");
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
		return intPeerID;
	}
	
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

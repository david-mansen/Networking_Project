import java.io.DataInputStream;
import java.io.IOException;
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
	
	public void run()
	{
		System.out.println("input connection running");
		waitForConnection();
		waitForHandshakeMessage();
	}
	
	public void waitForHandshakeMessage()
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
		}while(numBytes != 32);
	}
	
	public void waitForConnection()
	{
		if(serverSocket == null){
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
}

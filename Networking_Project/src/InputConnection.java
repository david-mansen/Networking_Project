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
	}
	
	public void waitForConnection()
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

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class OutputConnection extends Thread
{
	private Peer peer;
	private SwarmPeer targetPeer;
	private Socket clientSocket;
	
	public OutputConnection(Peer peer, SwarmPeer targetPeer)
	{
		this.peer=peer;
		this.targetPeer=targetPeer;
		clientSocket = null;
		this.start(); //start the thread
	}
	
	public void run()
	{
		System.out.println("Output connection running");
		establishConnection();
		if(clientSocket!=null)
		{
			HandshakeMessage handshake = new HandshakeMessage(peer.getPeerID());
			sendMessage(handshake);
			
		}
	}
	
	public void sendMessage(Message message)
	{
		String output = message.toString();
		byte[] outputBytes = output.getBytes();
		
		OutputStream outputStream;
		try
		{
			outputStream = clientSocket.getOutputStream();
			outputStream.write(outputBytes);
			outputStream.flush();
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	public void establishConnection()
	{
		try
		{
			clientSocket = new Socket(targetPeer.getHostName(),targetPeer.getPortNum());
			System.out.println("Peer "+peer.getPeerID()+" established connection with "+targetPeer.getPeerID());
		}
		catch(IOException exception)
		{
			System.err.println();
		}
	}
	
}

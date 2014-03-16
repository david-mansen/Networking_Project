import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class OutputConnection extends Thread
{
	private Peer peer;
	private SwarmPeer targetPeer;
	private Socket outputSocket;
	
	private boolean connectionEstablished;
	
	public OutputConnection(Peer peer, SwarmPeer targetPeer)
	{
		this.peer=peer;
		this.targetPeer=targetPeer;
		outputSocket = null;
		connectionEstablished = false;
		this.start(); //start the thread
	}
	
	public OutputConnection(Peer peer, Socket existingSocket)
	{
		this.peer=peer;
		outputSocket = existingSocket;
		connectionEstablished = false;
		this.start(); //start the thread
	}
	
	public void run()
	{
		System.out.println("Output connection running");
		establishConnection();
		if(outputSocket!=null)
		{
			connectionEstablished = true;
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
			outputStream = outputSocket.getOutputStream();
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
		if(outputSocket == null)
		{
			try
			{
				outputSocket = new Socket(targetPeer.getHostName(),targetPeer.getPortNum());
				System.out.println("Peer "+peer.getPeerID()+" established connection with "+targetPeer.getPeerID());
			}
			catch(IOException exception)
			{
				System.err.println();
			}
		}
	}

	public Socket getSocket() {
		return outputSocket;
	}
	
	public boolean getConnectionEstablished()
	{
		return connectionEstablished;
	}
	
}

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
	
	
	public OutputConnection(Peer peer, SwarmPeer targetPeer)
	{
		this.peer=peer;
		this.targetPeer=targetPeer;
		outputSocket = null;
		this.start(); //start the thread
	}
	
	public OutputConnection(Peer peer, Socket existingSocket)
	{
		this.peer=peer;
		outputSocket = existingSocket;
		this.start(); //start the thread
	}
	
	public void run()
	{
		System.out.println("Output connection running");
		establishConnection();
		if(outputSocket!=null)
		{
			HandshakeMessage handshake = new HandshakeMessage(peer.getPeerID());
			sendMessage(handshake);
		}
		peer.createInputConnection(new InputConnection(peer,outputSocket));
		peer.writeToLogFile("input connection created successfully");
		int i = 0;
		while(i!=1)
		{

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
				peer.writeToLogFile("Peer "+peer.getPeerID()+" established connection with "+targetPeer.getPeerID());
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
	
	
}

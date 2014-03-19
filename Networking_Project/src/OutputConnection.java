import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;


public class OutputConnection extends Thread
{
	private Peer peer;
	private SwarmPeer receiverPeer;
	private Socket outputSocket;
	
	public boolean done;
	
	public OutputConnection(Peer peer, SwarmPeer receiverPeer)
	{
		done=false;
		this.peer=peer;
		this.receiverPeer=receiverPeer;
		outputSocket = null;
		this.start(); //start the thread
	}
	
	public OutputConnection(Peer peer, Socket existingSocket, SwarmPeer receiverPeer)
	{
		done=true;
		this.peer=peer;
		this.receiverPeer = receiverPeer;
		outputSocket = existingSocket;
		this.start(); //start the thread
	}
	
	public void run()
	{
		establishConnection();
		peer.writeToLogFile("made it");
		if(outputSocket!=null)
		{
			HandshakeMessage handshake = new HandshakeMessage(peer.getPeerID());
			sendMessage(handshake);
		}
		if(peer.getInputConnection() == null)
		{
			peer.addInputConnection(new InputConnection(peer,outputSocket, receiverPeer));
		}
		TestMessage testMessage = new TestMessage("testing");
		sendMessage(testMessage);
		
		peer.decrementNumPeersDownloading();
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
			while(outputSocket==null || outputSocket.isConnected()==false)
			{
				try
				{
					outputSocket = new Socket(receiverPeer.getHostName(),receiverPeer.getPortNum());
					//peer.writeToLogFile("["+(new Date().toString())+"]: Peer [peer_ID "+peer.getPeerID()+
							//"] makes a connection to Peer [peer_ID "+targetPeer.getPeerID()+"].");
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

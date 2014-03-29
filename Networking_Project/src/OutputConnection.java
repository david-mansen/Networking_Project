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
		//peer.writeToLogFile("initiating connection...");
		establishConnection();
		//peer.writeToLogFile("initiated connection");

		if(outputSocket!=null)
		{
			HandshakeMessage handshake = new HandshakeMessage(peer.getPeerID());
			sendMessage(handshake);
		}
		if(peer.getInputConnection(receiverPeer) == null)
		{
			peer.addInputConnection(new InputConnection(peer,outputSocket, receiverPeer));
		}
		
		ChokeMessage chokeMessage = new ChokeMessage();
		sendMessage(chokeMessage);
		
		UnchokeMessage unchokeMessage = new UnchokeMessage();
		sendMessage(unchokeMessage);
		
		InterestedMessage interestedMessage = new InterestedMessage();
		sendMessage(interestedMessage);
		
		NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
		sendMessage(notInterestedMessage);
		
		BitfieldMessage bitfieldMessage = new BitfieldMessage(peer.getBitfield());
		sendMessage(bitfieldMessage);
		
		RequestMessage requestMessage = new RequestMessage(12345678);
		sendMessage(requestMessage);
		
		HaveMessage haveMessage = new HaveMessage(987654321);
		sendMessage(haveMessage);
		
		peer.decrementNumPeersDownloading();
		int i = 0;
		while(i!=1)
		{
			
		}
	}
	
	public void sendMessage(Message message)
	{
		byte[] outputBytes = message.toByteArray();
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
	

	public synchronized Socket getSocket() {
		return outputSocket;
	}
	
	public synchronized SwarmPeer getReceiverPeer()
	{
		return receiverPeer;
	}
	
}

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.System;
import java.util.concurrent.*;

public class OutputConnection extends Thread
{
	private Peer peer;
	private SwarmPeer receiverPeer;
	private Socket outputSocket;
	
	public boolean done;
	
	private BlockingQueue<Message> outputMessageQueue;
	private int numMessagesInQueue;
	
	public OutputConnection(Peer peer, SwarmPeer receiverPeer)
	{
		done=false;
		this.peer=peer;
		this.receiverPeer=receiverPeer;
		outputSocket = null;
		outputMessageQueue = new LinkedBlockingQueue<Message>();
		this.start(); //start the thread
	}
	
	public OutputConnection(Peer peer, Socket existingSocket, SwarmPeer receiverPeer)
	{
		done=true;
		this.peer=peer;
		this.receiverPeer = receiverPeer;
		outputSocket = existingSocket;
		outputMessageQueue = new LinkedBlockingQueue<Message>();
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
		
		if(peer.getPeerID() < receiverPeer.getPeerID()){
			while(!peer.receivedBitfield(receiverPeer)){}
		}
		BitfieldMessage bitfieldMessage = new BitfieldMessage(peer.getBitfield());
		sendMessage(bitfieldMessage);
		
		int i = 0;
		while(i!=1)
		{
			Message outputMessage = null;
			try {
				outputMessage = outputMessageQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			if(outputMessage != null)
			{
				sendMessage(outputMessage);
			}
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
	
	public void addMessageToQueue(Message message)
	{
		try {
			outputMessageQueue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Message getMessageFromQueue()
	{
		return outputMessageQueue.poll();
	}
	
	public synchronized String peekAtMessage()
	{
		Message m;
		m = outputMessageQueue.peek();
		return  m.toString();
	}
}

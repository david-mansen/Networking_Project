
public class PeerProcess1002 {
	public static void main(String[] arguments)
	{
		if(arguments.length!=1)
		{
			System.out.println("Must contain (1) argument");
			System.exit(0);
		}
		else
		{
			int peer_id = 1002;
			Peer thisPeer = new Peer(peer_id);
			System.exit(0);
		}
	}
}

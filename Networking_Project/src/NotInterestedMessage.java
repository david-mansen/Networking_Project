

public class NotInterestedMessage extends Message{
	
	private int length;
	private int type;
	//no payload
	
	public NotInterestedMessage()
	{
		length = 1;
		type = 3;
		//no payload
	}
	
	
	@Override
	public String toString()
	{
		String string = Integer.toString(length) + Integer.toString(type);
		return string;
	}


	@Override
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return null;
	}
}

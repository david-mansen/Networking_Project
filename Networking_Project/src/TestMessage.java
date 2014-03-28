import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class TestMessage extends Message{
	
	private String payload;
	
	public TestMessage(String payload)
	{
		this.payload = payload;
	}
	
	@Override
	public String toString()
	{
		return payload;
	}

	@Override
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return null;
	}
}

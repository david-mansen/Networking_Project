import java.nio.ByteBuffer;
import java.util.Arrays;

public class BitfieldMessage extends Message{
	
	private int length;
	private byte type;
	private boolean[] bitfield;
	
	
	public BitfieldMessage(boolean[] bitfield)
	{
		type = 0x05;
		this.bitfield = Arrays.copyOf(bitfield, bitfield.length);
		length = 1 + (int)Math.ceil((float)bitfield.length / 8.0f);
	}
	
	public BitfieldMessage(byte[] payload){
		bitfield = new boolean[payload.length*8];
		int j=0;
		for(int i=0;i<payload.length;i++){
			bitfield[j] = ((payload[i] & 0x01) != 0);
			bitfield[j+1] = ((payload[i] & 0x02) != 0);
			bitfield[j+2] = ((payload[i] & 0x04) != 0);
			bitfield[j+3] = ((payload[i] & 0x08) != 0);
			bitfield[j+4] = ((payload[i] & 0x10) != 0);
			bitfield[j+5] = ((payload[i] & 0x20) != 0);
			bitfield[j+6] = ((payload[i] & 0x40) != 0);
			bitfield[j+7] = ((payload[i] & 0x80) != 0);
			j = j+8;
		}
	}


	public String toString()
	{
		return null;
	}


	@Override
	public byte[] toByteArray() {
		
		//length
		ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
		lengthBuffer.putInt(length);
		byte[] lengthBytes = lengthBuffer.array();
		//payload
		byte payload[] = new byte[length-1];
		int j = 0;
		for(int i=0; i<=bitfield.length; i++)
		{
			if(bitfield.length <= j) break;
			if(bitfield[j] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 0));
			}
			
			if(bitfield.length <= j+1) break;
			if(bitfield[j+1] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 1));
			}
			
			if(bitfield.length <= j+2) break;
			if(bitfield[j+2] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 2));
			}
			
			if(bitfield.length <= j+3) break;
			if(bitfield[j+3] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 3));
			}
			
			if(bitfield.length <= j+4) break;
			if(bitfield[j+4] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 4));
			}
			
			if(bitfield.length <= j+5) break;
			if(bitfield[j+5] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 5));
			}
			
			if(bitfield.length <= j+6) break;
			if(bitfield[j+6] == true)
			{
				payload[i] = (byte) (payload[i]| (1 << 6));

			}
		
			if(bitfield.length <= j+7) break;
			if(bitfield[j+7] ==true)
			{
				payload[i] = (byte) (payload[i]| (1 << 7));
			}
			j = j+8;
		}
		
		//create return array
		byte[] bytes = new byte[4 + length];
		for(int i = 0; i<4; i++)
		{
			bytes[i] = lengthBytes[i];
		}
		bytes[4] = type;
		for(int i=0; i<payload.length; i++)
		{
			bytes[i+5] = payload[i];
		}
		
		return bytes;
	}
	
	
	public boolean[] getBitfield()
	{
		return bitfield;
	}
}

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.net.*;

public class Request{

	private int requestPiece;
	private long timeRequested;
	
	
	public Request(int requestPiece, long timeRequested){
		this.requestPiece = requestPiece;
		this.timeRequested = timeRequested;
	
	}
	
	public synchronized long getTimeRequested(){
		return timeRequested;
	}
	
	public synchronized int getRequestPiece(){
		return requestPiece;
	}

}


import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class DeltaTauComm {

	private  Session session;
	private static ChannelShell channel;
	private String username;
	private String password;
	private String hostname;
	private int sleep = 10;
	
	private static final String DEFAULT_USERNAME = "root";
	private static final String DEFAULT_PASSWORD = "deltatau";

	// Constructor (default username and password)
	public DeltaTauComm(String hostname)
	{
		this(hostname, DeltaTauComm.DEFAULT_USERNAME, DeltaTauComm.DEFAULT_PASSWORD);
	}
	
	public DeltaTauComm(String hostname, String username, String password)
	{
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}
	
	private Session getSession(){
	    if(session == null || !session.isConnected()){
	        session = connect(hostname,username,password);
	    }
	    return session;
	}
	
	private Channel getChannel(){
	    if(channel == null || !channel.isConnected()){
	        try{
	            channel = (ChannelShell)getSession().openChannel("shell");
	            channel.connect();
	
	        }catch(Exception e){
	            System.out.println("getChannel() error while opening channel: "+ e);
	        }
	    }
	    return channel;
	}
	
	private Session connect(String hostname, String username, String password){
	
	    JSch jSch = new JSch();
	
	    try {
	
	        session = jSch.getSession(username, hostname, 22);
	        //Properties config = new Properties(); 
	        //config.put("StrictHostKeyChecking", "no");
	        //session.setConfig(config);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(password);
	
	        System.out.println("connect() connecting SSH to " + hostname + " - Please wait for few seconds... ");
	        session.connect();
	        System.out.println("connect() connected!");
	    }catch(Exception e){
	        System.out.println("connect() an error occurred while connecting to "+hostname+": "+e);
	    }
	
	    return session;
	
	}
	
	/**
	 * Sends a single command and reads the response until a terminator is received and returns the 
	 * response without the terminator characters.
	 * @param {String} command
	 * @return {String}
	 */
	
	private String sendAndReceive(String command)
	{
		
		List<String> commands = new ArrayList<String>();
	    commands.add(command);
	    
		try{
	        Channel channel=getChannel();
	        sendCommands(channel, commands);
	        byte[] response = readChannelToTerminator(channel);
	        return new String(response);
	
	    }catch(Exception e){
	        System.out.println("sendAndReceive() an error ocurred during sendAndReceive: "+e);
	    }
		return "";
	}
	
	public void gpasciiInit()
	{
		
		List<String> commands = new ArrayList<String>();
	    commands.add("gpascii -2");
	    
		try{
	        Channel channel=getChannel();
	        sendCommands(channel, commands);
	        readChannelToAscii(channel);
	
	    }catch(Exception e){
	        System.out.println("sendAndReceive() an error ocurred during sendAndReceive: "+e);
	    }
	}
	
	public void gpasciiShortAnswers()
	{
		
		List<String> commands = new ArrayList<String>();
	    commands.add("echo 15");
	    
		try{
	        Channel channel=getChannel();
	        sendCommands(channel, commands);
	        readChannelToTerminator(channel);
	
	    }catch(Exception e){
	        System.out.println("gpasciiShortAnswers() an error ocurred during sendAndReceive: "+e);
	    }
	}
		
	
	/**
	 * Sends provided query command to the gpascii program. Waits until the InputStream of 
	 * the channel (data received from attached server) receives the expected response.  
	 * Parses the response to returns the answer String.  See readQueryAnswer for details about
	 * how the gpascii program responds to queries.  
	 * @param command
	 * @return
	 */
	public String gpasciiQuery(String command)
	{
		
		List<String> commands = new ArrayList<String>();
	    commands.add(command);
	    
		try{
	        Channel channel=getChannel();
	        sendCommands(channel, commands);
	        byte[] response = readChannelToAcknowledgement(channel);
	        return stringifyQueryResponse(response);
	
	    }catch(Exception e){
	        System.out.println("sendAndReceive() an error ocurred during sendAndReceive: "+e);
	    }
		return "";
	}
	
	/**
	 * Sends provided command to the gpascii program.  Waits until the InputStream of 
	 * the channel (data received from attached server) receives the expected response.  
	 * See readChannelToAcknowledgement for details about how the gpascii program responds to commands
	 * @param command
	 */
	
	public void gpasciiCommand(String command)
	{
		
		List<String> commands = new ArrayList<String>();
	    commands.add(command);
	    
		try{
	        Channel channel=getChannel();
	        sendCommands(channel, commands);
	        readChannelToAcknowledgement(channel);
	
	    }catch(Exception e){
	        System.out.println("sendAndReceive() an error ocurred during sendAndReceive: "+e);
	    }
	}
	
	private void executeCommands(List<String> commands){
	
	    try{
	        Channel channel=getChannel();
	
	        System.out.println("executeCommands() sending commands...");
	        sendCommands(channel, commands);
	
	        readChannelToTerminator(channel);
	        System.out.println("executeCommands() finished sending commands!");
	
	    }catch(Exception e){
	        System.out.println("executeCommands() an error ocurred during executeCommands: "+e);
	    }
	}
	
	private void sendCommands(Channel channel, List<String> commands){
	
	    try{
	        PrintStream out = new PrintStream(channel.getOutputStream());
	
	        //out.println("#!/bin/bash");
	        for(String command : commands){
	            out.println(command);
	        }
	        //out.println("exit");
	
	        out.flush();
	    }catch(Exception e){
	        System.out.println("sendCommands() error while sending commands: "+ e);
	    }
	
	}
	
	/**
	 * Returns a primitive byte[] with terminator bytes (13 and 10) removed from the end.  
	 * @param original
	 * @return byte[]
	 */
	private byte[] removeTerminatorBytesFromEnd(byte[] original)
	{
		
		if (original.length == 0) 
		{
			return original;
		}
		byte carriageReturn = 13;
		byte lineFeed = 10;
		while (
			original[original.length - 1] == carriageReturn ||
			original[original.length - 1] == lineFeed
		)
		{
			//System.out.println("removeTerminatorBytesFromEnd removing byte");
			original = Arrays.copyOfRange(original, 0, original.length - 1);
		}
		
		return original;		

	}
	
	/**
	 * Returns true if the provided primitive byte[] contains a terminator byte (13 or 10) 
	 * at the end.  
	 * @param original
	 * @return boolean
	 */
	private boolean containsTerminatorByteAtEnd(byte[] bytes)
	{
		if (bytes.length == 0) {
			return false;
		}
		
		byte carriageReturn = 13;
		byte lineFeed = 10;
		
		if  (
			bytes[bytes.length - 1] == carriageReturn ||
			bytes[bytes.length - 1] == lineFeed
		)
		{
			return true;
		}
		else 
		{
			return false;
		}
		
	}
	
	/**
	 * Returns true if the final three bytes of the provided byte[] are [6 13 10] 
	 * in order
	 * @param byte[] bytes
	 * @return boolean
	 */
	private boolean containsAcknowledgementAndTerminatorBytesAtEnd(byte[] bytes)
	{
		if (bytes.length < 3) 
		{
			return false;
		}
		
		byte[] bytesLastThree = Arrays.copyOfRange(bytes, bytes.length - 3, bytes.length);
		byte[] bytesToCheck = {6, 13, 10};
		
		if (Arrays.equals(bytesLastThree, bytesToCheck)) 
		{
			return true;
		}
		else 
		{
			return false;
		}
		
	}
	
	/**
	 * Returns true if the final bytes of the provided byte[] are 
	 * [65, 83, 67, 73, 73, 32, 73, 110, 112, 117, 116, 13, 10]
	 * "ASCII Input\r\n"  in order
	 * @param byte[] bytes
	 * @return boolean
	 */
	private boolean containsAsciiInputAndTerminatorBytesAtEnd(byte[] bytes)
	{
		
		byte[] bytesDesired = {65, 83, 67, 73, 73, 32, 73, 110, 112, 117, 116, 13, 10};
		
		if (bytes.length < bytesDesired.length) 
		{
			return false;
		}
		
		byte[] bytesToCheck = Arrays.copyOfRange(bytes, bytes.length - bytesDesired.length, bytes.length);

		if (Arrays.equals(bytesDesired, bytesToCheck)) 
		{
			return true;
		}
		else 
		{
			return false;
		}
		
	}
	
	
	/**
	 * Reads the inputStream of the channel (data received from the attached server)
	 * until a terminator byte (13 or 10) is reached.  It then removes the terminator
	 * bytes from the response, converts to a String and returns the string
	 * @param channel
	 */
	
	private byte[] readChannelToTerminator(Channel channel){
	
	    byte[] buffer = new byte[1024];
	    byte[] bufferFilled = new byte[1024];
	    
	    int bytesRead = 0;
	    int bytesReadSum = 0;
	    
	    try{
	        InputStream in = channel.getInputStream();
	        String line = "";
	        while (true){
	        	
	        		// in.avaialable() > 0 does not happen immediately; it takes time for 
	        		// the data packets to arrive.  
	        		// can't break out of loop immediately once in.available() is not > 0
	        		// Need to keep checking in.available() until we have read back a 
	        		// terminator
	        	
	        		//System.out.println("readChannelToTerminator while loop");
	            while (in.available() > 0) {
	            	
	            		// Second arg of in.read() is the start offset in array buffer
	            		// at which the data is written
	            	
	            		// read() Returns the total number of bytes read into the buffer, 
	            		// or -1 if there is no more data
	            	
	                bytesRead = in.read(buffer, bytesReadSum, in.available());
	                bytesReadSum += bytesRead;
	                
	                //System.out.println("readChannelToTerminator() read packet from inputStream");
	                
	                if (bytesRead < 0) {
	                    // nothing else to read right now so can break out of this
	                		// while loop (that reads most recently received data packet)
	                		// This doesn't mean we have received a terminator character
	                		// so can't break out of outer loop yet
	                		break;
	                }	                
	                
	            }
	            
	            	
	            if (channel.isClosed()){
	                break;
	            }
	            
	            // Trim buffer to the range that has been filled so far
	            // so we can check for terminator byte at the end
	            bufferFilled = Arrays.copyOfRange(buffer, 0, bytesReadSum);
	            
	            
	            if (containsTerminatorByteAtEnd(bufferFilled))
	            {
            			//System.out.println("readChannelToTerminator() Breaking. Terminator byte has been received!");
            			//System.out.println(new String(bufferFilled));
            			// The terminator byte has been received!
            			// Can break out of the high-level while loop that is waiting
            			// for the terminator byte
	            		break; 
	            }
	            
	            try 
	            {
	                Thread.sleep(sleep);
	            } 
	            catch (Exception ee)
	            {
	            	
	            } 
	        }
	        
	        // buffer now contains terminator byte at the end
	        
            // Remove terminator bytes
            //byte [] bufferWithoutTerminators = removeTerminatorBytesFromEnd(bufferFilled);
            
            // Convert to string and print
            //line = new String(bufferWithoutTerminators);
            
            //System.out.println(line);
            
            return bufferFilled;
	    }
	    catch(Exception e)
	    {
	        System.out.println("readChannelToTerminator() Error while reading channel output: "+ e);
	    }
	    
	    return new byte[0];
	
	}
	
	
	
	/**
	 * The response from the server for a command has the form:
	 * [commandBytes] 13 10 [acknowledgementByte] 13 10
	 * Where the acknowledgement byte === 6
	 * 
	 * The response from the server for a query has the form: 
	 * [queryBytes] 13 10 [answerBytes] 13 10 [acknowledgementByte] 13 10
	 * where the acknowledgement byte === 6. This method returns [answerBytes] converted to a String.
	 * 
	 * This method reads the inputStream of the channel (data received from the attached server)
	 * until the bytes [6 13 10] are received and returns the received byte[]  
	 * @param channel
	 */
	
	private byte[] readChannelToAcknowledgement(Channel channel){
		
	    byte[] buffer = new byte[1024];
	    byte[] bufferFilled = new byte[1024];
	    
	    int bytesRead = 0;
	    int bytesReadSum = 0;
	    
	    try{
	        InputStream in = channel.getInputStream();
	        String line = "";
	        while (true){
	        	
	        		// in.avaialable() > 0 does not happen immediately; it takes time for 
	        		// the data packets to arrive.  
	        		// can't break out of loop immediately once in.available() is not > 0
	        		// Need to keep checking in.available() until we have read back a 
	        		// terminator
	        	
	        		//System.out.println("readChannelToTerminator while loop");
	            while (in.available() > 0) {
	            	
	            		// Second arg of in.read() is the start offset in array buffer
	            		// at which the data is written
	            	
	            		// read() Returns the total number of bytes read into the buffer, 
	            		// or -1 if there is no more data
	            	
	                bytesRead = in.read(buffer, bytesReadSum, in.available());
	                bytesReadSum += bytesRead;
	                
	                //System.out.println("readChannelToTerminator() read packet from inputStream");
	                
	                if (bytesRead < 0) {
	                    // nothing else to read right now so can break out of this
	                		// while loop (that reads most recently received data packet)
	                		// This doesn't mean we have received a terminator character
	                		// so can't break out of outer loop yet
	                		break;
	                }	                
	                
	            }
	            
	            	
	            if (channel.isClosed()){
	                break;
	            }
	            
	            
	            // Trim buffer to the range that has been filled so far
	            // so we can check the bytes at the end
	            bufferFilled = Arrays.copyOfRange(buffer, 0, bytesReadSum);
	            
	            if (containsAcknowledgementAndTerminatorBytesAtEnd(bufferFilled))
	            {
            			//System.out.println("Breaking. Acknowledgement byte has been received!");
            			
            			// The acknowledgement byte has been received!
            			// Can break out of the high-level while loop that is waiting
            			// for the terminator byte
	            		break; 
	            }
	            
	            try 
	            {
	                Thread.sleep(sleep);
	            } 
	            catch (Exception ee)
	            {
	            	
	            } 
	        }
	        
	        // buffer now contains terminator and acknowledgement byte at the end. We're done
	        
	        return bufferFilled;
	        
	    }
	    catch(Exception e)
	    {
	        System.out.println("readChannelToTerminator() Error while reading channel output: "+ e);
	    }
	    
	    return new byte[0];
	    	
	}
	
	
	/**
	 * This method reads the inputStream of the channel (data received from the attached server)
	 * until the byte equivalent of "ASCII Input\r\n" is received
	 * ([65, 83, 67, 73, 73, 32, 73, 110, 112, 117, 116, 13, 10])
	 * This method should be used after the initial gpascii -2 command is sent to start the text
	 * interpreter on the PowerPmac.  Once this response is recieved, the ASCII interpreter
	 * is ready for commands 
	 * @param channel
	 */
	
	private byte[] readChannelToAscii(Channel channel){
		
	    byte[] buffer = new byte[1024];
	    byte[] bufferFilled = new byte[1024];
	    
	    int bytesRead = 0;
	    int bytesReadSum = 0;
	    
	    try{
	        InputStream in = channel.getInputStream();
	        String line = "";
	        while (true){
	        	
	        		// in.avaialable() > 0 does not happen immediately; it takes time for 
	        		// the data packets to arrive.  
	        		// can't break out of loop immediately once in.available() is not > 0
	        		// Need to keep checking in.available() until we have read back a 
	        		// terminator
	        	
	        		//System.out.println("readChannelToTerminator while loop");
	            while (in.available() > 0) {
	            	
	            		// Second arg of in.read() is the start offset in array buffer
	            		// at which the data is written
	            	
	            		// read() Returns the total number of bytes read into the buffer, 
	            		// or -1 if there is no more data
	            	
	                bytesRead = in.read(buffer, bytesReadSum, in.available());
	                bytesReadSum += bytesRead;
	                
	                //System.out.println("readChannelToTerminator() read packet from inputStream");
	                
	                if (bytesRead < 0) {
	                    // nothing else to read right now so can break out of this
	                		// while loop (that reads most recently received data packet)
	                		// This doesn't mean we have received a terminator character
	                		// so can't break out of outer loop yet
	                		break;
	                }	                
	                
	            }
	            
	            	
	            if (channel.isClosed()){
	                break;
	            }
	            
	            
	            // Trim buffer to the range that has been filled so far
	            // so we can check the bytes at the end
	            bufferFilled = Arrays.copyOfRange(buffer, 0, bytesReadSum);
	            
	            if (containsAsciiInputAndTerminatorBytesAtEnd(bufferFilled))
	            {
            			//System.out.println("readChannelToAscii() breaking.");
            			
            			// The acknowledgement byte has been received!
            			// Can break out of the high-level while loop that is waiting
            			// for the terminator byte
	            		break; 
	            }
	            
	            try 
	            {
	                Thread.sleep(sleep);
	            } 
	            catch (Exception ee)
	            {
	            	
	            } 
	        }
	        
	        // buffer now contains terminator and acknowledgement byte at the end. We're done
	        
	        return bufferFilled;
	        
	    }
	    catch(Exception e)
	    {
	        System.out.println("readChannelToTerminator() Error while reading channel output: "+ e);
	    }
	    
	    return new byte[0];
	    	
	}
	
	/**
	 * The response from the server for a query has the form: 
	 * [queryBytes] 13 10 [answerBytes] 13 10 [acknowledgementByte] 13 10
	 * where the acknowledgement byte === 6. This method returns [answerBytes] converted to a String.
	 * 
	 * This method removes the last five bytes ([13 10 6 13 10])
	 * from the end of the received byte array. It then searches for the terminator byte 
	 * sequence following [queryBytes], in order to isolate [answerBytes], convert answerBytes[] to 
	 * a String and return the string. 
	 */
	
	private String stringifyQueryResponse(byte[] response)
	{
        // Remove [13 10 6 13 10] bytes from the end of buffer
        response = Arrays.copyOfRange(response, 0, response.length - 5);
        
        byte carriageReturn = 13;
		
        int i = 0;
        for (i = 0; i < response.length; i++) 
        {
        		if (response[i] == carriageReturn) {
        			break;
        		}
        }
        
        // Remove [queryBytes] 13 10 from response
        byte[] answer = Arrays.copyOfRange(response, i + 2, response.length);
         
        // Convert to string and print
        return new String(answer);
	}
	
	public void close(){
	    channel.disconnect();
	    session.disconnect();
	    System.out.println("close() Disconnected channel and session");
	}	
}

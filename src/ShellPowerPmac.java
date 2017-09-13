/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/**
 * This program enables you to connect to sshd server and get the shell prompt.
 *   $ CLASSPATH=.:../build javac Shell.java 
 *   $ CLASSPATH=.:../build java Shell
 * You will be asked username, hostname and passwd. 
 * If everything works fine, you will get the shell prompt. Output may
 * be ugly because of lacks of terminal-emulation, but you can issue commands.
 *
 */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;

import java.io.*;
import java.net.*;


public class ShellPowerPmac{
  public static void main(String[] args){
    
    try{
      JSch jsch=new JSch();
      String user = "root";
      String host = "192.168.20.23";
      
      Session session=jsch.getSession(user, host, 22);
      session.setPassword("deltatau");
      
      // Only need to call session.setUserInfo() and provide an instance
      // of something that implements com.jcraft.jsch.UserInfo

      // It must not be recommended, but if you want to skip host-key check,
      // invoke following,
      session.setConfig("StrictHostKeyChecking", "no");

      //session.connect();
      session.connect(30000);   // making a connection with timeout.

      Channel channel=session.openChannel("shell");

      // Enable agent-forwarding.
      //((ChannelShell)channel).setAgentForwarding(true);

      /*
      // a hack for MS-DOS prompt on Windows.
      channel.setInputStream(new FilterInputStream(System.in){
          public int read(byte[] b, int off, int len)throws IOException{
            return in.read(b, off, (len>1024?1024:len));
          }
        });
       */

      channel.setOutputStream(System.out);
      channel.setInputStream(System.in);
      
      // Output (send) to the socket 
      // The output stream of the channel (on the client)
      // sends information to the server.  Wrap in a PrintWriter
      // so we can programatically send commands to the server
      
      PrintWriter out =
          new PrintWriter(channel.getOutputStream(), true);
      
      

      // The input stream of the channel (on the client) 
      // receives information from the server
      
      /*
      BufferedReader in =
          new BufferedReader(
              new InputStreamReader(channel.getInputStream()));
      */


      //channel.connect();
      channel.connect(3*1000);
      
      // Programatically send the pwd command
      //out.println("pwd");
      //channel.sendSignal("pwd\r\n");
      //out.println("gpascii -2\r\n");
      
    }
    catch(Exception e){
      System.out.println("There was an exception" + e);
    }
  }




  class SftpUserInfo implements UserInfo {
    
    String password = "For/24an";

    public String getPassphrase() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwd) {
        password = passwd;
    }

    public boolean promptPassphrase(String message) {
        return false;
    }

    public boolean promptPassword(String message) {
        return false;
    }

    public boolean promptYesNo(String message) {
        return true;
    }

    public void showMessage(String message) {
    }
  }
}

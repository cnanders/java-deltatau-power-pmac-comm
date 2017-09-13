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

public class ShellLocalhost{
  public static void main(String[] args){
    
    try{
      JSch jsch=new JSch();
      String user = "cnanderson";
      String host = "localhost";
      
      Session session=jsch.getSession(user, host, 22);

      session.setPassword("For/24an");

      //UserInfo ui = new UserInfoDeltaTau();
      //session.setUserInfo(ui);

      // It must not be recommended, but if you want to skip host-key check,
      // invoke following,
      session.setConfig("StrictHostKeyChecking", "no");

      //session.connect();
      session.connect(30000);   // making a connection with timeout.

      Channel channel=session.openChannel("shell");

      // Enable agent-forwarding.
      //((ChannelShell)channel).setAgentForwarding(true);

      channel.setInputStream(System.in);
      /*
      // a hack for MS-DOS prompt on Windows.
      channel.setInputStream(new FilterInputStream(System.in){
          public int read(byte[] b, int off, int len)throws IOException{
            return in.read(b, off, (len>1024?1024:len));
          }
        });
       */

      channel.setOutputStream(System.out);

      
      // Choose the pty-type "vt102".
      //channel.setPtyType("vt100");
      

      /*
      // Set environment variable "LANG" as "ja_JP.eucJP".
      ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
      */

      //channel.connect();
      channel.connect(3*1000);
    }
    catch(Exception e){
      System.out.println(e);
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

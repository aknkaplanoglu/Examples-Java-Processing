/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eyetracker.OCR;


// SaferExec.java
// Andrew Davison, May 2010, ad@fivedots.coe.psu.ac.th

/* Utilizing ideas from:

    "When Runtime.exec() won't"
    Michael C. Daconta, http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html

    "Five Common java.lang.Process Pitfalls"
    Kyle W. Cartmell, http://kylecartmell.com/?p=9

    "Java exec - execute system processes with Java ProcessBuilder and Process" (parts 1-3)
    Alvin Alexander, http://www.devdaily.com/java/java-exec-processbuilder-process-1

    + my own thoughts :)

    ------- Usage ---------
    On Windows:
      javac SaferExec.java
      java SaferExec dir

    On Linux:
      gcj --main=SaferExec -o SaferExec SaferExec.java
      ./SaferExec ls

    or in an application:
      SaferExec se = new SaferExec();
      String result = se.exec("dir *.java");

    ----------------------
    I haven't tested this code on a Mac. If anyone does, and finds problems, please contact me.
    Of course, the same goes for Windows/Linux users. I'll mention you in updates to the
    documentation.
*/

import java.io.*;
import java.util.*;



public class SaferExec
{
  private static final int MAX_WAIT = 5;   
         // a command can take at most 5 secs before being interrupted


  /* The following are commands that need to be invoked inside a shell.
     They may still not work because of incorrect privileges, missing 
     environment settings, differing shell versions, etc.
     but invoking them in a shell may work :)
  */
  private static final String[] WIN_CMDS = {      // shell commands in Windows
      "assoc", "call", "cd", "cls", "color", "copy", "date", "del", "dir", "echo", 
      "endlocal", "erase", "exit", "for", "ftype", "goto", "if", "md", "move", 
      "path", "pause", "popd", "prompt", "pushd", "ren", "rd", "set", "setlocal", 
      "shift", "start", "time", "title", "type", "ver", "verify", "vol"  };

  private static final String[] UNIX_CMDS = {      // for sh 
       "alias", "bg", "break", "breaksw", "case", "cd", "chdir", "continue", 
       "default", "dirs", "echo", "eval", "exec", "exit", "fg", "foreach", "glob", 
       "goto", "hashstat", "history", "if", "jobs", "limit", "login", "logout", 
       "nice", "nohup", "notify", "onintr", "popd", "pushd", "rehash", "repeat", 
       "set", "setenv", "shift", "source", "stop ", "suspend", "switch", "time", 
       "umask", "unalias", "unhash", "unlimit", "unset", "unsetenv", "wait", 
       "while", "@" };

  private static final String[] MAC_CMDS = {     // not tested
       "alias", "bg", "bind", "break", "builtin", "caller", "case", "cd", "command", 
       "complete", "continue", "declare", "dirs", "disown", "echo", "enable", "eval", 
       "exec", "exit", "export", "fg", "for", "getopts", "hash", "history", "if", 
       "jobs", "let", "local", "logout", "popd", "printf", "pwd", "read", "readonly", 
       "return", "select", "set", "shift", "shopt", "source", "suspend", "tail", "test", 
       "times", "trap", "type", "ulimit", "unalias", "unset", "until", "wait", "while"  };



  private int waitTime;   // time to wait in seconds before interrupting a command
  private volatile boolean isInterrupted;

  public SaferExec()
  {  waitTime = MAX_WAIT; }


  public SaferExec(int time)   // in secs
  {  waitTime = time;  }



  public String exec(String... command)
  // evaluate command, returning its result as a string
  {
    if (command.length == 1) {    // input is one string, which may need tokenizing
      // System.out.println("Tokenizing \"" + command + "\"");
      String[] toks = command[0].split("\\s+");    // split across 1 or more spaces
      return execToks(toks, false);
    }
    else  // command is made up of mulltiple tokens already
      return execToks(command, false);
  }  // end of exec() 



  public String execV(String... command)
  // return command's result *and* its exit value
  {
    if (command.length == 1) {
      String[] toks = command[0].split("\\s+"); 
      return execToks(toks, true);
    }
    else 
      return execToks(command, true);
  }  // end of execV() 



  
  private String execToks(String[] commandToks, boolean wantExitValue)
  // execute the command tokens
  {
    String[] cmdStr = checkOSCmd(commandToks);
    printCmd(cmdStr);

    int exitValue = 1;    // assume the worst: non-0 means an error

    // interrupt this thread if it does not finish within the wait time period
    Timer timer = new Timer(true);
    InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread(), this);
    timer.schedule(interrupter, waitTime*1000);   // wait time is converted to ms

    ProcessBuilder launcher = new ProcessBuilder(cmdStr);
    launcher.redirectErrorStream(true);  // combine stderr and output from the process

    Process p = null;
    InputStream in = null;
    StringBuffer sb = new StringBuffer();
    isInterrupted = false;
    try {
      p = launcher.start();

      // save process' output in a string
      in = p.getInputStream();   // this stream is the input to this app _from_ the process
      int c;
      while (((c = in.read()) != -1) && !isInterrupted)   // data is read byte-by-byte
        sb.append( (char)c );

      // if (isInterrupted)
      //   System.out.println("Interrupted since output took longer than " + waitTime + " secs");

      // GCJ hangs in waitFor() unless all 3 streams are explicitly closed
      in.close();
      p.getOutputStream().close();
      p.getErrorStream().close();

      exitValue = p.waitFor();
    }
    catch (InterruptedIOException e) {
      System.out.println("Interrupted since output did not finish within " + waitTime + " secs");
    }
    catch (InterruptedException e) {
      System.out.println("Interrupted since command did not finish within " + waitTime + " secs");
    } 
    catch(Exception e) {  
      System.out.println(e);  
    }  
    finally {
      timer.cancel();
      Thread.interrupted();   // clear the interrupt flag in case the
                              // interrupter executed after waitFor() returned but before timer.cancel()
                              // was called. There's also Sun bug 6420270 to worry about.
      if (in != null)
        try
        {  in.close(); }
        catch (IOException e) { }  // ignore this one

      if (p != null)
        p.destroy();     // stop the process

      if (wantExitValue)   
        sb.append("" + exitValue + "\n");   // add exit value to output

      if (exitValue != 0)   // report a problem
        System.out.println("Problem executing command; exit value = " + exitValue);
    }
    return sb.toString();
  }  // end of execToks()


  public void setInterruptFlag()
  // secondary way to force an interrupt
  {  isInterrupted = true;  }


  private void printCmd(String[] cmdStr)
  // print the command tokens
  {
    System.out.print("Command = { ");
    for (int i = 0; i < cmdStr.length; i++) 
      System.out.print("\"" + cmdStr[i] + "\" ");
    System.out.println("}");
  }  // end of printCmd()



  // ----------------- OS specific -----------------------------------

  private String[] checkOSCmd(String[] cmdStr)
  // modify the command if its a shell command
  {
    String cmd = cmdStr[0].toLowerCase();

    if (isWindows()) {
      for (String osCmd : WIN_CMDS)
        if (cmd.equals(osCmd))
          return addWinCmd(cmdStr);
    }
    else if (isUnix()) {
      for (String osCmd : UNIX_CMDS)
        if (cmd.equals(osCmd))
          return addUnixCmd(cmdStr);
    }
    else if (isMac()) {   // not tested
      for (String osCmd : MAC_CMDS)
        if (cmd.equals(osCmd))
          return addMacCmd(cmdStr);
    }

    return cmdStr;
  }  // end of checkOSCmd()



  private boolean isWindows()
  { String os = System.getProperty("os.name").toLowerCase();
	return (os.indexOf("win") >= 0); 
  }


  private  boolean isUnix()
  // unix or linux
  { String os = System.getProperty("os.name").toLowerCase();
	return ((os.indexOf("nix") >=0) || (os.indexOf("nux") >=0));
  }


  private boolean isMac()   // not tested
  { String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("mac") >= 0); 
  }


  private String[] addWinCmd(String[] cmdStr)
  // modify command to be executed using cmd /c in Windows
  {
    System.out.println("Adding \"cmd /c\" to Windows command");
    String[] useCmd = new String[] {"cmd", "/c"};
    return concat(useCmd, cmdStr);
  }  // end of addWinCmd()


  private String[] addUnixCmd(String[] cmdStr) 
  // modify command to be executed using sh -c in UNIX
  {
    System.out.println("Adding \"/bin/sh -c\" to Unix command");
    String[] useCmd = new String[] {"/bin/sh", "-c"};
    return concat(useCmd, cmdStr);
  }  // end of addUnixCmd()


  private String[] addMacCmd(String[] cmdStr)   // not tested
  // modify command to be executed using sh -c in OS X
  {
    System.out.println("Adding \"/bin/sh -c\" to Mac command");
    String[] useCmd = new String[] {"/bin/sh", "-c"};
    return concat(useCmd, cmdStr);
  }  // end of addMacCmd()



  private String[] concat(String[] s1, String[] s2)
  // create a new string array that is s1 + s2
  {
    String[] newS = new String[s1.length + s2.length];
    int i;
    for (i=0; i < s1.length; i++)
       newS[i] = s1[i];
    for (i=0; i < s2.length; i++)
       newS[s1.length + i] = s2[i];
    return newS;
  }  // end of addWinCmd()


  // -------------------------------------------------------------

  private class InterruptTimerTask extends TimerTask
  // send an interrupt to the target thread after a specified time
  {
    private Thread target = null;
    private SaferExec executor;

    public InterruptTimerTask(Thread t, SaferExec se)
    {  target = t;   
       executor = se;
    }

    public void run()
    { executor.setInterruptFlag();
      target.interrupt();  
    }

  }  // end of InterruptTimerTask class



  // ------------------------------ test rig ----------------------

  public static void main(String[] args)
  {
    SaferExec se = new SaferExec();          // or include a wait time in secs
    System.out.println( se.exec(args) );    // or use execV()
  }  // end of main()


}  // end of SaferExec class


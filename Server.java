
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;
public class Server {
	public static ServerSocket serverSocket=null;
	public static Socket socket = null;
	public static InputStream in = null;
	public static OutputStream os =null;
    public static FileInputStream fis = null;
    public static BufferedInputStream bis = null;
     
     public static int port=4444;
	public static void main(String[] args) throws IOException 
	{
		int count;
	     boolean acceptFlag=false;
		try{
			if(args.length!=0)
				port= Integer.parseInt(args[0]);
		serverSocket = new ServerSocket(port);
		}catch(ArrayIndexOutOfBoundsException aiobe)
		{
			System.out.println("Please specify the port number as the first argument");
			aiobe.printStackTrace();
		}
		while(!acceptFlag)
		{
			try{
					socket = serverSocket.accept();
					acceptFlag=true;
			System.out.println("Connection established");
			}catch(SocketTimeoutException timeOut)
			{
				System.out.println("Connection timed out....Reconnecting.....");
				continue;
			}
		}
	        String command = readMsgFromClient(in,socket);
	        try{
					        while(!command.equals("exit"))
					        {
								  if(command.equals("index"))
								  {
								       walk(args[1]);
								       writeMsgToClient("EOF\n",os,socket);
								  }
								        
								else if(command.contains("get"))
												{
													System.out.println("get reached");
													downloadFile();
								        		 }
								  		System.out.print("\t Command : ");
								         command =readMsgFromClient(in,socket).trim();
								      
					        }
	        }catch(ArrayIndexOutOfBoundsException aiob)
	        {
	        	System.out.println("Please specify the root path of the server as the 2nd command line argument");
	        	aiob.printStackTrace();
	        }
	        catch(FileNotFoundException fntf)
	        {
	        	System.out.println("Please enter a valid file to download from the server by making use of the index command");
	        	fntf.printStackTrace();
	        }finally{
	        		if(in!=null) in.close();
	        		if(socket!=null)socket.close();
	        		if(serverSocket!=null)serverSocket.close();
	        }
	}
	  public static void walk( String path ) throws IOException {

	        File root = new File( path );
	        File[] list = root.listFiles();//Array consists of all the files in that directory.

	        if (list == null) return;//Base condition

	        for ( File f : list ) {
	            if ( f.isDirectory() ) {
	                walk( f.getAbsolutePath() );//For each child of root which is a directory,recursively call this routine.
	              writeMsgToClient( "Dir:" + f.getAbsoluteFile() +"\n" ,os,socket);
	            }
	            else {//Print the details of the file which isn't a directory.
	                writeMsgToClient( "File: " + f.getAbsoluteFile() +"\n",os,socket );
	            }
	        }
	    }
	  public static String readMsgFromClient(InputStream in,Socket socket) throws IOException
	  {
		  in = socket.getInputStream();
		  byte[] bytes = new byte[16*1024];
	        in.read(bytes);
	        String msg=new String(bytes).trim();
	        bytes=null;
	        return msg;
	  }
	  public static void writeMsgToClient(String msg,OutputStream os,Socket socket) throws IOException
	  {
		  System.out.print(msg);
		  byte[]  bytes=msg.getBytes();
		  os=socket.getOutputStream();
		  os.write(bytes,0,bytes.length);
		  os.flush();
	  }
	  public static void downloadFile() throws IOException
	  {
		  writeMsgToClient("Enter the file name",os,socket);
  		String targetFile =readMsgFromClient(in,socket);
  		File file = new File(targetFile);			        		
  		in=new FileInputStream(file);
  		writeMsgToClient(targetFile + " available",os,socket);			
  		String fileSize=String.valueOf(file.length());
  		//System.out.println("file Size " + fileSize + "Last Index" + targetFile.lastIndexOf("/") +"targetFile.lenght " +  targetFile.length());
  		String name=targetFile.substring(targetFile.lastIndexOf("/")+1, targetFile.length());
  		writeMsgToClient(fileSize,os,socket);
  		readMsgFromClient(in,socket);
  		writeMsgToClient(name,os,socket);
  		OutputStream out = socket.getOutputStream();	  
  		 System.out.println("Sending file " + targetFile + "....");
  		 byte [] file2bytesArr  = new byte [(int)file.length()];
           fis = new FileInputStream(file);
           bis = new BufferedInputStream(fis);
           bis.read(file2bytesArr,0,file2bytesArr.length);
           os = socket.getOutputStream();
           os.write(file2bytesArr,0,file2bytesArr.length);								          
           out.flush();
           System.out.println("Done.");
    
	  }
}


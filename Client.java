
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
public class Client {
    public static final String IP = "127.0.0.1"; 
    public static int port =4444;
    public static  Socket socket=null;
    public static Scanner sc=null;
    public static OutputStream out=null;
    public static  InputStream in=null;
    public static FileOutputStream fos = null;
    public static BufferedOutputStream bos = null;
    public static DataInputStream dis;
    public static void main(String[] args) throws IOException 
    {
    try{
        if(args.length>0)
            connectToServer(IP,Integer.parseInt(args[0]));
        else
            connectToServer(IP,port);
            }catch(ArrayIndexOutOfBoundsException arr)
            {
                System.out.println("Please specify the port as the first command line argument");
                arr.printStackTrace();
            }
    
        menu();
        String msg;
        sc=new Scanner(System.in);
        msg = sc.nextLine();
        writeMsgToServer(msg,out);
        while(!msg.equals("exit"))
        {
            switch(msg)
            {
            case "index":
                   //writeMsgToServer(msg,out);
                    readLongMsgFromServer(in,socket);
                    break;
            case "get":
              //  writeMsgToServer(msg,out);
                System.out.println(readMsgFromServer(in,socket));
                String targetFileName=sc.nextLine();
                writeMsgToServer(targetFileName,out);
                String availabilitymsg=readMsgFromServer(in,socket);
                System.out.println(availabilitymsg);
                String fsize=readMsgFromServer(in,socket);
                int fileSize = Integer.parseInt(fsize);
                writeMsgToServer("file size received",out);
                String fileName=readMsgFromServer(in,socket);
                System.out.println(fileName);
                downloadFile(fileName,fileSize);
                break;
            }
       
          System.out.print("Command\t");
           msg = sc.nextLine();
           writeMsgToServer(msg,out);
        }
        
        
    }
    
    
    
    public static boolean connectToServer(String IP,int port)
    {
        boolean connectionFlag=false;
        while(!connectionFlag)
        {
            try{
                socket=new Socket(IP,port);
                connectionFlag=true;
            }catch(Exception e)
                {
                    continue;
                }
        }
        System.out.println("Connection established");
        return connectionFlag;
    }
    public static void writeMsgToServer(String msg,OutputStream os) throws IOException
    {
        os=socket.getOutputStream();
        os.write(msg.getBytes());
        os.flush();
        
    }
    public static void menu()
    {
    	System.out.println("---------------MENU-----------");
        System.out.println("Send your first message to the server");
        System.out.println("--------------COMMANDS----------");
        System.out.println("\tIndex - ls on the server \n" + "\tget to initiate a file downlaod ");
        System.out.print("\tCommand : ");
    }
    public static void readLongMsgFromServer(InputStream in,Socket socket) throws IOException
    {
        String msg;
        in=socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        while(!isr.ready());
        while((msg=br.readLine())!=null)
        {
            msg=msg.trim();
            if(msg.equals("EOF"))
            {
                
                return;
            }
            System.out.println(msg);
        }
    
    }
    public static String readMsgFromServer(InputStream in,Socket socket) throws IOException 
    {
      in = socket.getInputStream();
      byte[] bytes = new byte[16*1024];
        in.read(bytes);
        String msg=new String(bytes).trim();
        bytes=null;
        return msg;
    }
    public static void downloadFile(String fileName,int fileSize) throws IOException
    {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] filebytes = new byte[fileSize];
        dis.read(filebytes, 0, fileSize);       
        System.out.println("File downloaded " + " size : "+fileSize);
        fos.write(filebytes, 0, fileSize);
        fos.close();
    }
}
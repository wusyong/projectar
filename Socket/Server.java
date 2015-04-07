// Server portion of a client/server stream-socket connection. 
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.*;
import org.apache.hadoop.conf.Configuration;


public class Server 
{
   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   private ServerSocket server; // server socket
   private Socket connection; // connection to client
   private int counter = 1; // counter of number of connections
   private String angle;

   public static Configuration conf;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2222");
        conf.set("hbase.zookeeper.quorum", "hbasemaster");
    }

   // set up and run server 
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while ( true ) 
         {
            try 
            {
               waitForConnection(); // wait for a connection
               getStreams(); // get input & output streams
               processConnection(); // process connection
            } // end try
            catch ( EOFException eofException ) 
            {
               System.out.println( "Server terminated connection" );
            } // end catch
            finally 
            {
               closeConnection(); //  close connection
               ++counter;
            } // end finally
         } // end while
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch

      
   } // end method runServer

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {
      System.out.println("Waiting for connection");
      connection = server.accept(); // allow server to accept connection            
      System.out.println( "Connection " + counter + " received from: " +
                      connection.getInetAddress().getHostName() );
   } // end method waitForConnection

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      System.out.println( "Got I/O streams" );
   } // end method getStreams

   // process connection with client
   private void processConnection() throws IOException
   {
      String message = "Connection successful";
      sendData( message ); // send connection successful message

      do // process messages sent from client
      { 
         try // read message and display it
         {
            angle = ( String ) input.readObject(); // read new message
            System.out.println( angle ); // display message
            //Scanner scanner = new Scanner(System.in);
            //String str = scanner.nextLine();

            HTable table = new HTable(conf, "UserTableTmp");
            HTable gazer = new HTable(conf, "gazer_table_tmp");
            

            
            //PUT
            Put p = new Put(Bytes.toBytes("MAC1")); 
            p.add(Bytes.toBytes("angle"), Bytes.toBytes(""),
                  Bytes.toBytes("420"));
            table.put(p); 
        
            //GET
            Get g = new Get(Bytes.toBytes("sec1")); 
            Result r = gazer.get(g); 
            byte[] value = r.getValue(Bytes.toBytes("text"),
                                       Bytes.toBytes(""));
            String valueStr = Bytes.toString(value); 
            System.out.println("GET: " + valueStr);

            sendData(valueStr);
            sendData("TERMINATE");
            break;
            
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            System.out.println( "Unknown object type received" );
         } // end catch

      } while ( !message.equals( "CLIENT>>> TERMINATE" ) );
   } // end method processConnection

   // close streams and socket
   private void closeConnection() 
   {
      System.out.println( "\nTerminating connection\n" );
      //setTextFieldEditable( false ); // disable enterField

      try 
      {
         output.close(); // close output stream
         input.close(); // close input stream
         connection.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to client
   private void sendData( String message )
   {
      try // send object to client
      {
         output.writeObject( message );
         output.flush(); // flush output to client
         System.out.println( "SERVER>>> " + message );
      } // end try
      catch ( IOException ioException ) 
      {
         System.out.println( "Error writing object" );
      } // end catch
   } // end method sendData


} // end class Server
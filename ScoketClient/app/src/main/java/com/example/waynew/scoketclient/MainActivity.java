package com.example.waynew.scoketclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView textResponse;
    EditText editTextClient;
    Button buttonConnect, buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextClient = (EditText)findViewById(R.id.client);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(editTextClient.getText().toString());
                    myClientTask.execute();
                }};

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstText;
        String response = "";

        MyClientTask(String cli){
            dstText = cli;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String message = "";
            Socket socket = null;
            ObjectOutputStream output = null;
            ObjectInputStream input = null;


            try // connect to server, get streams, process connection
            {
                response = "Attempting connection\n" ;

                // create Socket to make connection to server
                socket = new Socket("140.113.110.21" ,12345 );

                // display connection information
                response += "Connected to: " +
                        socket.getInetAddress().getHostName() + "\n" ;

                // set up output stream for objects
                output = new ObjectOutputStream( socket.getOutputStream() );
                output.flush(); // flush output buffer to send header information

                // set up input stream for objects
                input = new ObjectInputStream( socket.getInputStream() );

                response += "Got I/O streams\n";
                do // process messages sent from server
                {
                    try // read message and display it
                    {
                        message = ( String ) input.readObject(); // read new message
                        response +=  message +"\n" ; // display message

                        try // send object to server
                        {
                            if(message.equals("SERVER>>> Connection successful")) {

                                WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                                WifiInfo info = manager.getConnectionInfo();
                                String address = info.getMacAddress();

                                output.writeObject( "CLIENT>>> " + address );
                                output.flush(); // flush data to output
                            }
                            else {
                                output.writeObject( "CLIENT>>> " + dstText );
                                output.flush(); // flush data to output
                            }
                        } // end try
                        catch ( IOException ioException )
                        {
                            response += "Error writing object\n" ;
                        } // end catch

                    } // end try
                    catch ( ClassNotFoundException classNotFoundException )
                    {
                        response += "Unknown object type received\n" ;
                    } // end catch

                } while ( !message.equals( "SERVER>>> TERMINATE" ) );
            } // end try
            catch ( IOException ioException )
            {
                ioException.printStackTrace();
            } // end catch
            finally
            {
                System.out.println( "Closing connection" );
                //setTextFieldEditable( false ); // disable enterField

                try
                {
                    output.close(); // close output stream
                    input.close(); // close input stream
                    socket.close(); // close socket
                } // end try
                catch ( IOException ioException )
                {
                    ioException.printStackTrace();
                } // end catch
            } // end finally
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }

}
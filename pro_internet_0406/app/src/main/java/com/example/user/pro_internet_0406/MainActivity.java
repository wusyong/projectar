package com.example.user.pro_internet_0406;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView textResponse;
    EditText editTextClient;
    Button buttonConnect, buttonClear;ImageView iv;
    SensorManager sm;
    Sensor s1,s2;
    SampleSensorEventListener sse;
    String message = "";
    String mes_temp;

    Bitmap bmp;
    float[] av =new float [3];
    float[] mv =new float [3];
    float[] R1=new float [16];
    float []R2=new float [16];
    float[]I=new float [16];
    float []v=new float [3];
    float d;
    float d2=0;

    TextView TT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextClient = (EditText)findViewById(R.id.client);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.NO_GRAVITY);
        // setContentView(ll);

        //bmp = BitmapFactory.decodeResource(getResources(), R.drawable.key);
        iv = new ImageView(this);
        iv.setImageBitmap(bmp);
        iv.setBackgroundColor(Color.TRANSPARENT);
        TT = new TextView(this);
        ll.addView(TT);
        ll.setBackgroundColor(Color.TRANSPARENT);
        TT.layout(0, 0, TT.getRight(), TT.getBottom());
        sse = new SampleSensorEventListener();
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
    }

    protected void onResume()
    {
        super.onResume();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        s1 = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        s2 = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(sse, s1, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(sse, s2, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause()
    {
        super.onPause();
        sm.unregisterListener(sse, s1);
        sm.unregisterListener(sse, s2);
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(editTextClient.getText().toString());
                    myClientTask.execute();

                   // if(mes_temp.regionMatches(0,"www",0,3)==true) {
                      //  TextView T2 = (TextView)findViewById(R.id.view_main);
                     //   T2.setText(mes_temp);
                   // }

                        //url_1 = message.substring(10);

                }};

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstText;
        String response = "";

        MyClientTask(String cli){
            dstText = cli;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //String message = "";
            Socket socket = null;
            ObjectOutputStream output = null;
            ObjectInputStream input = null;


            try // connect to server, get streams, process connection
            {
                response = "Attempting connection\n" ;

                // create Socket to make connection to server
                socket = new Socket("140.113.110.14" ,12345 );

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

                                output.writeObject( "CLIENT>>> " + address + "\n" + "direction: :" + d + "\n" );
                                output.flush(); // flush data to output
                            }
                            else {

                                if(message.regionMatches(0,"www",0,3))
                                {
                                    mes_temp = message.substring(0,message.length());

                                    mes_temp = "http://" + mes_temp;
                                    //response += mes_temp +" ggg"+ "\n";
                                    Intent intent1 = new Intent();
                                    intent1.setAction(Intent.ACTION_VIEW);
                                    intent1.setData(Uri.parse(mes_temp));
                                    startActivity(intent1);
                                }

                                output.writeObject( (int)d +"\n" );
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

    class SampleSensorEventListener implements SensorEventListener
    {
        public void onSensorChanged(SensorEvent e)
        {
            switch(e.sensor.getType())
            {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mv = e.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    av = e.values.clone();
                    break;
            }
            if(mv!=null && av!=null)
            {
                SensorManager.getRotationMatrix(R1,I,av,mv);
                SensorManager.remapCoordinateSystem(R1,SensorManager.AXIS_X,SensorManager.AXIS_Z,R2);
                SensorManager.getOrientation(R2,v);
                d = (float) Math.toDegrees(v[0]);
                Matrix m = new Matrix();

           /*     if( d2-d>170 || d2-d<-170 )
                {
                    //MyClientTask myClientTask = new MyClientTask(editTextClient.getText().toString());
                   // myClientTask.execute();
                    d2=d;
                    if(d2<20 && d2>-10 )
                    {
                     //   if(mes_temp.regionMatches(0,"www",0,3)==true) {

                            Intent intent1 = new Intent();
                            intent1.setAction(Intent.ACTION_VIEW);
                            intent1.setData(Uri.parse("http://www.google.com"));
                            startActivity(intent1);
                      //  }
                    }
                }*/

            }
        }
        public void onAccuracyChanged(Sensor s,int accuracy){}
    }

}
import java.io.*;
import java.net.*;
public class ServerThread extends Thread {
 
    private String usermsg = "";
    private BufferedReader clientin;
 
    @SuppressWarnings("unused")
    private ServerThread() {
        // can't use Constructor
    }
 
    public ServerThread(BufferedReader in) {
        clientin = in;// 
    }
 
    @Override
    public void run() {
        // loop to listen User's input
        while (true) {
            try {
                usermsg = clientin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("user said:" + usermsg);
             
            if (usermsg=="bye"){
                System.out.println("disconnected");
                try {
                    clientin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500); // rest 0.5 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
             
        }
    }
 
}
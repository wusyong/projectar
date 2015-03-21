import java.io.*;
import java.net.*;

public class SocketServer {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocket ss = null;
        Socket socket = null;
        BufferedReader in;
        PrintWriter out;
        BufferedReader keyboardInput;
        Thread serverthread;
        try {
            ss = new ServerSocket(5023);// sever 5023port
            System.out.println("waitting...");
            socket = ss.accept(); 
            System.out.println("client connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverthread = new ServerThread(in);
        serverthread.start();// listen
        if (serverthread.isAlive())
            System.out.println("thread opened succes");
 
        // send msg to client
        if (true) {
            keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            String str=keyboardInput.readLine();
            System.out.println("server said:" + str);
 
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);// UTF-8
            out.println(str);
            out.flush();
        }
 
        keyboardInput.close();
        out.close();
        socket.close();
        ss.close();
    }
 
}
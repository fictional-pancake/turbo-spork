package fictionalpancake.turbospork.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    public ClientHandler(Socket s) {
        socket = s;
        try {
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(dis.readChar());
            }
        } catch(IOException e) {
            if(!(e instanceof EOFException)) {
                e.printStackTrace();
            }
            System.out.println(socket.getInetAddress()+" has disconnected");
        }
    }
}

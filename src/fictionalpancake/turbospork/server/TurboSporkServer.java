package fictionalpancake.turbospork.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TurboSporkServer {
    public static void main(String[] args) {
        try {
            String portvar = System.getenv("PORT");
            int port = 3000;
            if(portvar != null) {
                port = Integer.parseInt(portvar);
            }
            ServerSocket ss = new ServerSocket(port);
            while(true) {
                Socket s = ss.accept();
                System.out.println(s.getInetAddress());
                ClientHandler ch = new ClientHandler(s);
                ch.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

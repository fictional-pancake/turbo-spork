package fictionalpancake.turbospork;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;

public class TurboSpork {

    private static URI serverURI = null;

    /**
     * The main method.
     *
     * @param args an array of arguments.  If there's anything in it, use the first as the server URI
     */
    public static void main(String[] args) {
        if(args.length > 0) {
            try {
                serverURI = new URI(args[0]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        JFrame login = LoginWindow.open();
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static URI getServerURI() {
        if(serverURI == null) {
            try {
                serverURI = new URI("ws://turbo-spork.herokuapp.com");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return serverURI;
    }
}

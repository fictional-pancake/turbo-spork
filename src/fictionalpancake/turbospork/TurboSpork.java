package fictionalpancake.turbospork;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;

public class TurboSpork {

    private static URI serverURI = null;

    /**
     * The main method.
     *
     * @param args an array of arguments.  If there's anything in it, use the first as the server URI and the next two as login credentials
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                serverURI = new URI(args[0]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        String[] credentials = null;
        if(args.length == 2) {
            credentials = new String[]{args[1]};
        }
        else if(args.length > 2) {
            credentials = new String[]{args[1],args[2]};
        }

        JFrame login = LoginWindow.open(credentials);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static URI getServerURI() {
        if (serverURI == null) {
            try {
                serverURI = new URI("ws://turbo-spork.herokuapp.com");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return serverURI;
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}

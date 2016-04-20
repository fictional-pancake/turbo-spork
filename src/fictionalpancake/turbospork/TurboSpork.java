package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TurboSpork {

    /**
     * The main method.
     *
     * @param args an array of arguments.  If there's anything in it, use the first as the server URI and the next two as login credentials
     */
    public static void main(String[] args) {
        String uri = null;
        if (args.length > 0) {
            uri = args[0];
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            e.printStackTrace();
        }

        String[] credentials = null;
        if (args.length == 2) {
            credentials = new String[]{args[1]};
        } else if (args.length > 2) {
            credentials = new String[]{args[1], args[2]};
        }

        JFrame login = LoginWindow.open(credentials, uri);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static Color getColorForOwner(int owner) {
        if (owner < 0) {
            return Color.lightGray;
        } else if (owner < GameConstants.COLORS.length) {
            return GameConstants.COLORS[owner];
        } else {
            Random r = new Random(owner);
            return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        }
    }

    public static int toInt(Object o) {
        if(o instanceof Integer) {
            return ((int) o);
        }
        else if(o instanceof Long) {
            return ((Long) o).intValue();
        }
        else {
            return Integer.parseInt(o.toString());
        }
    }
}

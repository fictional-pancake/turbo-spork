package fictionalpancake.turbospork.gui;

import javax.swing.*;

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



    public static java.awt.Color convertColor(fictionalpancake.turbospork.paint.Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

}

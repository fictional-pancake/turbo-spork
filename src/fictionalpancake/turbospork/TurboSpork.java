package fictionalpancake.turbospork;

import javax.swing.*;

public class TurboSpork {
    public static void main(String[] args) {
        /*JFrame frame = new JFrame();
        JLabel unfinishedLabel = new JLabel("This isn't a game yet");
        frame.add(unfinishedLabel);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);*/
        JFrame login = LoginWindow.open();
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

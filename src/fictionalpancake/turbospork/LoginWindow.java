package fictionalpancake.turbospork;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_76;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class LoginWindow extends JPanel implements ActionListener {

    public static final int INPUT_SIZE = 20;

    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private JProgressBar bar;
    private JLabel errorLabel;

    private JFrame window;

    public LoginWindow(JFrame frame) {
        this.window = frame;

        setLayout(new GridBagLayout());

        username = new JTextField();
        username.setColumns(INPUT_SIZE);
        username.addActionListener(this);
        GridBagConstraints usernameConstraints = new GridBagConstraints();
        usernameConstraints.gridy = 2;
        usernameConstraints.gridwidth = 3;

        password = new JPasswordField();
        password.setColumns(INPUT_SIZE);
        password.addActionListener(this);
        GridBagConstraints passwordConstraints = new GridBagConstraints();
        passwordConstraints.gridy = 3;
        passwordConstraints.gridwidth = 3;

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 4;

        bar = new JProgressBar();
        bar.setVisible(false);
        bar.setIndeterminate(true);
        GridBagConstraints progressConstraints = new GridBagConstraints();
        progressConstraints.gridy = 0;
        progressConstraints.gridwidth = 3;

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.red);
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridy = 1;
        labelConstraints.gridwidth = 3;

        add(username, usernameConstraints);
        add(password, passwordConstraints);
        add(loginButton, buttonConstraints);
        add(bar, progressConstraints);
        add(errorLabel, labelConstraints);
    }

    public static JFrame open() {
        JFrame frame = new JFrame("Login to Turbo-Spork");
        JPanel panel = new LoginWindow(frame);
        frame.add(panel);
        frame.setSize(250, 150);
        frame.setVisible(true);
        return frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        errorLabel.setText("");
        username.setEnabled(false);
        password.setEnabled(false);
        loginButton.setEnabled(false);
        bar.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GameHandler gh = new GameHandler(new DataListener<String>() {
                    @Override
                    public void onData(String data) {
                        int colonPos = data.indexOf(':');
                        String firstPart = data.substring(0, colonPos);
                        String lastPart = data.substring(colonPos + 1);
                        if(firstPart.equals("join")) {
                            JFrame dialog = new JFrame();
                            JLabel label = new JLabel("Welcome to the absence of a game!");
                            dialog.add(label);
                            dialog.setSize(250, 50);
                            dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            dialog.setVisible(true);
                            window.setVisible(false);
                            System.out.println(data);
                        }
                        else {
                            username.setEnabled(true);
                            password.setEnabled(true);
                            loginButton.setEnabled(true);
                            bar.setVisible(false);
                            if(firstPart.equals("error")) {
                                errorLabel.setText(lastPart);
                            }
                            else {
                                errorLabel.setText("Unable to parse server response.");
                                System.err.println("Unable to parse response:");
                                System.err.println(data);
                            }
                        }
                    }
                });
                try {
                    gh.connectBlocking();
                    gh.send("auth:"+username.getText()+":"+String.valueOf(password.getPassword()));
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    System.exit(-2);
                }
            }
        }).start();
    }
}

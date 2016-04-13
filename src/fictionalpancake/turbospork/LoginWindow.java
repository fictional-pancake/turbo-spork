package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;

public class LoginWindow extends JPanel implements ActionListener {

    public static final int INPUT_SIZE = 20;

    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private JProgressBar bar;
    private JLabel errorLabel;
    private JComboBox<String> uriBox;

    private JFrame window;

    public LoginWindow(JFrame frame) {
        this.window = frame;

        setLayout(new GridBagLayout());

        username = new JTextField();
        username.setColumns(INPUT_SIZE);
        username.addActionListener(this);
        GridBagConstraints usernameConstraints = new GridBagConstraints();
        usernameConstraints.gridy = 3;
        usernameConstraints.gridwidth = 3;
        username.requestFocus();

        password = new JPasswordField();
        password.setColumns(INPUT_SIZE);
        password.addActionListener(this);
        GridBagConstraints passwordConstraints = new GridBagConstraints();
        passwordConstraints.gridy = 4;
        passwordConstraints.gridwidth = 3;

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 5;

        bar = new JProgressBar();
        bar.setVisible(false);
        bar.setIndeterminate(true);
        GridBagConstraints progressConstraints = new GridBagConstraints();
        progressConstraints.gridy = 1;
        progressConstraints.gridwidth = 3;

        errorLabel = new JLabel();
        errorLabel.setForeground(Color.red);
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridy = 2;
        labelConstraints.gridwidth = 3;

        uriBox = new JComboBox<String>(new String[]{"turbo-spork.herokuapp.com", "turbo-spork-test.herokuapp.com", "localhost:5000"});
        uriBox.setEditable(true);
        GridBagConstraints boxConstraints = new GridBagConstraints();
        boxConstraints.gridy = 0;
        boxConstraints.gridwidth = 3;

        add(username, usernameConstraints);
        add(password, passwordConstraints);
        add(loginButton, buttonConstraints);
        add(bar, progressConstraints);
        add(errorLabel, labelConstraints);
        add(uriBox, boxConstraints);
    }

    public static JFrame open(String[] credentials, String uri) {
        JFrame frame = new JFrame("Login to Turbo-Spork");
        LoginWindow panel = new LoginWindow(frame);
        frame.add(panel);
        frame.setSize(250, 150);
        if(credentials != null) {
            if(credentials.length == 2) {
                panel.username.setText(credentials[0]);
                panel.password.setText(credentials[1]);
                panel.actionPerformed(null);
            }
            else {
                throw new IllegalArgumentException("credentials array must contain 2 values");
            }
        }
        if(uri != null) {
            panel.uriBox.setSelectedItem(uri);
        }
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
            public GameHandler gh;

            @Override
            public void run() {
                URI uri = null;
                try {
                    uri = new URI("ws://"+uriBox.getSelectedItem());
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
                System.out.println(uri);
                gh = new GameHandler(new DataListener<String>() {
                    @Override
                    public void onData(String data) {
                        int colonPos = data.indexOf(':');
                        String firstPart = data.substring(0, colonPos);
                        String lastPart = data.substring(colonPos + 1);
                        if(firstPart.equals("join")) {
                            window.setVisible(false);
                            GameWindow.open(gh);
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
                }, uri);
                try {
                    gh.connectBlocking();
                    gh.send("auth:"+username.getText()+":"+String.valueOf(password.getPassword())+":"+GameConstants.PROTOCOL_VERSION);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    System.exit(-2);
                } catch(NotYetConnectedException e) {
                    errorLabel.setText("Could not connect to server.");
                    username.setEnabled(true);
                    password.setEnabled(true);
                    loginButton.setEnabled(true);
                    bar.setVisible(false);
                    System.err.println("failed connection");
                }
            }
        }).start();
    }
}

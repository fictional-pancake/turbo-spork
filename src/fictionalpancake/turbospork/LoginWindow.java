package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JPanel implements ActionListener {

    public static final int INPUT_SIZE = 20;

    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private JProgressBar bar;
    private JLabel errorLabel;

    public LoginWindow() {
        setLayout(new GridBagLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

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
        JPanel panel = new LoginWindow();
        frame.add(panel);
        frame.setSize(250, 150);
        frame.setVisible(true);
        return frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        username.setEnabled(false);
        password.setEnabled(false);
        loginButton.setEnabled(false);
        bar.setVisible(true);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                errorLabel.setText("This isn't a real login dialog");
                username.setEnabled(true);
                password.setEnabled(true);
                loginButton.setEnabled(true);
                bar.setVisible(false);
            }
        }).start();
    }
}

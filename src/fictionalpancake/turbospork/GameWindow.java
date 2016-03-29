package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JPanel {
    public GameWindow(GameHandler gameHandler) {
        setLayout(new BorderLayout());
        RoomInfoPanel leftPanel = new RoomInfoPanel(gameHandler);
        add(leftPanel, BorderLayout.WEST);
    }

    public static void open(GameHandler gameHandler) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GameWindow(gameHandler));
        frame.setVisible(true);
    }

    private class RoomInfoPanel extends JPanel implements RoomInfoListener, ActionListener {
        private JList<String> userList;
        private JButton joinBtn;
        private GameHandler gameHandler;
        public RoomInfoPanel(GameHandler gameHandler) {
            this.gameHandler = gameHandler;
            gameHandler.setRoomInfoListener(this);
            setLayout(new BorderLayout());
            userList = new JList<String>(new DefaultListModel<String>());
            add(userList, BorderLayout.CENTER);
            joinBtn = new JButton("Join Room");
            add(joinBtn, BorderLayout.NORTH);
            joinBtn.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            gameHandler.openJoinDialog();
        }

        @Override
        public void onLeftRoom(String id) {
            if(id.equals(gameHandler.getUserID())) {
                ((DefaultListModel)userList.getModel()).removeAllElements();
                joinBtn.setText("Join Room");
            }
            else {
                ((DefaultListModel)userList.getModel()).removeElement(id);
            }
        }

        @Override
        public void onJoinedRoom(String id) {
            if(id.equals(gameHandler.getUserID())) {
                joinBtn.setText("Switch Room");
            }
            ((DefaultListModel<String>)userList.getModel()).addElement(id);
        }
    }
}

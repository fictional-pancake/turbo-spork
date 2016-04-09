package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JPanel {
    public GameWindow(GameHandler gameHandler) {
        setLayout(new BorderLayout());
        RoomInfoPanel leftPanel = new RoomInfoPanel(gameHandler);
        add(leftPanel, BorderLayout.WEST);
        GameMainPanel rightPanel = new GameMainPanel(gameHandler);
        add(rightPanel, BorderLayout.CENTER);
        new Thread(new RepaintThread(rightPanel)).start();
    }

    public static void open(GameHandler gameHandler) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new GameWindow(gameHandler));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(Math.min(1024, screenSize.width), Math.min(768, screenSize.height));
        frame.setVisible(true);
    }

    private class RoomInfoPanel extends JPanel implements RoomInfoListener, ActionListener {
        private JList<String> userList;
        private JButton joinBtn;
        private JButton startBtn;
        private GameHandler gameHandler;

        public RoomInfoPanel(GameHandler gameHandler) {
            this.gameHandler = gameHandler;
            gameHandler.setRoomInfoListener(this);
            setLayout(new BorderLayout());
            userList = new JList<String>(new DefaultListModel<String>());
            userList.setCellRenderer(new UserListCellRenderer());
            add(userList, BorderLayout.CENTER);
            joinBtn = new JButton();
            add(joinBtn, BorderLayout.NORTH);
            joinBtn.addActionListener(this);
            startBtn = new JButton("Start Game");
            startBtn.setEnabled(false);
            add(startBtn, BorderLayout.SOUTH);
            startBtn.addActionListener(this);
            updateButtonState();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == joinBtn) {
                gameHandler.openJoinDialog();
            } else if (e.getSource() == startBtn) {
                gameHandler.startGame();
            } else {
                System.err.println("Unrecognized button.");
            }
        }

        @Override
        public void onLeftRoom(String id) {
            if (id.equals(gameHandler.getUserID())) {
                ((DefaultListModel<String>) userList.getModel()).removeAllElements();
            } else {
                ((DefaultListModel<String>) userList.getModel()).removeElement(id);
            }
            updateButtonState();
        }

        @Override
        public void onJoinedRoom(String id) {
            ((DefaultListModel<String>) userList.getModel()).addElement(id);
            System.out.println(id + " joined");
            updateButtonState();
        }

        @Override
        public void onGameStart() {
            updateButtonState();
        }

        @Override
        public void onGameEnd() {
            updateButtonState();
        }

        private void updateButtonState() {
            DefaultListModel<String> listModel = ((DefaultListModel<String>) userList.getModel());
            if (listModel.isEmpty()) {
                joinBtn.setText("Join Room");
            } else {
                joinBtn.setText("Switch Room");
                String leader = listModel.getElementAt(0);
                String userID = gameHandler.getUserID();
                System.out.println("Leader is " + leader);
                System.out.println("You are " + userID);
                startBtn.setEnabled(leader.equals(userID) && !gameHandler.isInProgress());
            }
        }

        private class UserListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component tr = super.getListCellRendererComponent(list, value, index, false, false);
                tr.setForeground(TurboSpork.getColorForOwner(index));
                return tr;
            }
        }
    }

}

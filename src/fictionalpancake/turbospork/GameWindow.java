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
        private JButton matchBtn;
        private JPanel topPanel;
        private GameHandler gameHandler;

        public RoomInfoPanel(GameHandler gameHandler) {
            this.gameHandler = gameHandler;
            gameHandler.setRoomInfoListener(this);
            setLayout(new BorderLayout());
            userList = new JList<String>(new DefaultListModel<String>());
            userList.setCellRenderer(new UserListCellRenderer());
            add(userList, BorderLayout.CENTER);
            topPanel = new JPanel();
            topPanel.setLayout(new GridLayout(2, 1));
            joinBtn = new JButton();
            matchBtn = new JButton("Play");
            topPanel.add(joinBtn);
            topPanel.add(matchBtn);
            add(topPanel, BorderLayout.NORTH);
            joinBtn.addActionListener(this);
            matchBtn.addActionListener(this);
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
            } else if (e.getSource() == matchBtn) {
                gameHandler.join("matchme");
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
                startBtn.setEnabled(false);
            } else {
                joinBtn.setText("Switch Room");
                String leader = listModel.getElementAt(0);
                String userID = gameHandler.getUserID();
                System.out.println("Leader is " + leader);
                System.out.println("You are " + userID);
                startBtn.setEnabled(leader.equals(userID) && !gameHandler.isInProgress() && gameHandler.isMatchMeRoom());
            }
        }

        private class UserListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component tr = super.getListCellRendererComponent(list, value, index, gameHandler.adjustForRemoved(index) == gameHandler.getPosition(), false);
                tr.setForeground(TurboSpork.getColorForOwner(gameHandler.adjustForRemoved(index)));
                return tr;
            }
        }
    }

}

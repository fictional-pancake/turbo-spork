package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GameWindow(gameHandler));
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
                ((DefaultListModel) userList.getModel()).removeAllElements();
            } else {
                ((DefaultListModel) userList.getModel()).removeElement(id);
            }
            updateButtonState();
        }

        @Override
        public void onJoinedRoom(String id) {
            ((DefaultListModel<String>) userList.getModel()).addElement(id);
            System.out.println(id+" joined");
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
    }

    private class GameMainPanel extends JPanel {
        private GameHandler gameHandler;
        private int xOffset;
        private int yOffset;
        private double scale;

        public GameMainPanel(GameHandler gameHandler) {
            this.gameHandler = gameHandler;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            checkScaling();
            g.clearRect(0, 0, getWidth(), getHeight());
            if (gameHandler.isInProgress()) {
                List<Node> nodes = gameHandler.getNodes();
                for (Node node : nodes) {
                    g.setColor(getColorForOwner(node.getOwner()));
                    g.fillOval(convertX(node.getX() - GameConstants.NODE_RADIUS), convertY(node.getY() - GameConstants.NODE_RADIUS), convertSize(GameConstants.NODE_RADIUS * 2), convertSize(GameConstants.NODE_RADIUS * 2));
                }
            }
            else {
                String lastWinner = gameHandler.getLastWinner();
                if(lastWinner != null) {
                    g.setFont(new Font("SansSerif", Font.ITALIC, convertSize(GameConstants.WIN_TEXT_SIZE)));
                    drawStringCenter(g, lastWinner+" wins!", convertX(GameConstants.FIELD_SIZE/2), convertY(GameConstants.FIELD_SIZE/2));
                }
            }
        }

        private Color getColorForOwner(int owner) {
            if(owner < 0) {
                return Color.lightGray;
            }
            else if(owner < GameConstants.COLORS.length) {
                return GameConstants.COLORS[owner];
            }
            else {
                Random r = new Random();
                return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
            }
        }

        private int convertX(int x) {
            return (int) (xOffset + x * scale);
        }

        private int convertY(int y) {
            return (int) (yOffset + y * scale);
        }

        private int convertSize(int s) {
            return (int) (s * scale);
        }

        public void checkScaling() {
            int minDimension;
            if (getWidth() < getHeight()) {
                xOffset = 0;
                yOffset = (getHeight() - getWidth()) / 2;
                minDimension = getWidth();
            } else {
                xOffset = (getWidth() - getHeight()) / 2;
                yOffset = 0;
                minDimension = getHeight();
            }
            scale = minDimension / GameConstants.FIELD_SIZE;
        }

        private void drawStringCenter(Graphics g, String string, int centerX, int centerY) {
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(string, centerX-metrics.stringWidth(string)/2, centerY-metrics.getHeight()/2);
        }
    }
}

package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(Math.min(1024, screenSize.width), Math.min(768, screenSize.height));
        frame.setVisible(true);
    }

    private class RoomInfoPanel extends JPanel implements RoomInfoListener, ActionListener {
        private JList userList;
        private JButton joinBtn;
        private JButton startBtn;
        private GameHandler gameHandler;

        public RoomInfoPanel(GameHandler gameHandler) {
            this.gameHandler = gameHandler;
            gameHandler.setRoomInfoListener(this);
            setLayout(new BorderLayout());
            userList = new JList(new DefaultListModel());
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
            ((DefaultListModel) userList.getModel()).addElement(id);
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
            DefaultListModel listModel = ((DefaultListModel) userList.getModel());
            if (listModel.isEmpty()) {
                joinBtn.setText("Join Room");
            } else {
                joinBtn.setText("Switch Room");
                String leader = (String) listModel.getElementAt(0);
                String userID = gameHandler.getUserID();
                System.out.println("Leader is " + leader);
                System.out.println("You are " + userID);
                startBtn.setEnabled(leader.equals(userID) && !gameHandler.isInProgress());
            }
        }
    }

    private class GameMainPanel extends JPanel implements MouseMotionListener {
        private GameHandler gameHandler;
        private int xOffset;
        private int yOffset;
        private double scale;
        private int mouseY;
        private int mouseX;

        public GameMainPanel(GameHandler gameHandler) {
            this.gameHandler = gameHandler;
            addMouseMotionListener(this);
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);
            Graphics2D g = ((Graphics2D) graphics);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Stroke defaultStroke = g.getStroke();
            checkScaling();
            g.clearRect(0, 0, getWidth(), getHeight());
            if (gameHandler.isInProgress()) {
                List<Node> nodes = gameHandler.getNodes();
                boolean hasShownOutline = false;
                for (Node node : nodes) {
                    g.setColor(getColorForOwner(node.getOwner()));
                    int x = convertX(node.getX() - GameConstants.NODE_RADIUS);
                    int y = convertY(node.getY() - GameConstants.NODE_RADIUS);
                    int d = convertSize(GameConstants.NODE_RADIUS * 2);
                    g.fillOval(x, y, d, d);
                    if(!hasShownOutline && isMouseOverNode(node)) {
                        hasShownOutline = true;
                        g.setColor(Color.darkGray);
                        g.setStroke(new BasicStroke(convertSize(GameConstants.OUTLINE_SIZE)));
                        g.drawOval(x, y, d, d);
                        g.setStroke(defaultStroke);
                    }
                }
            } else {
                String lastWinner = gameHandler.getLastWinner();
                if (lastWinner != null) {
                    g.setFont(new Font("SansSerif", Font.ITALIC, convertSize(GameConstants.WIN_TEXT_SIZE)));
                    drawStringCenter(g, lastWinner + " wins!", convertX(GameConstants.FIELD_SIZE / 2), convertY(GameConstants.FIELD_SIZE / 2));
                }
            }
        }

        private Color getColorForOwner(int owner) {
            if (owner < 0) {
                return Color.lightGray;
            } else if (owner < GameConstants.COLORS.length) {
                return GameConstants.COLORS[owner];
            } else {
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

        private int convertSize(double s) {
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
            scale = ((double) minDimension) / GameConstants.FIELD_SIZE;
        }

        private void drawStringCenter(Graphics g, String string, int centerX, int centerY) {
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(string, centerX - metrics.stringWidth(string) / 2, centerY - metrics.getHeight() / 2);
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }

        private boolean isMouseOverNode(Node node) {
            return TurboSpork.distance(mouseX, mouseY, convertX(node.getX()), convertY(node.getY())) <= convertSize(GameConstants.NODE_RADIUS);
        }
    }
}

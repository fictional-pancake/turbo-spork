package fictionalpancake.turbospork.gui;

import fictionalpancake.turbospork.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JPanel implements DataListener<String> {

    private GameHandler gameHandler;
    private final NiceAction ACTION_OPEN_JOIN_DIALOG = new NiceAction("Switch Room", KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            openJoinDialog(false);
        }
    };
    private final NiceAction ACTION_OPEN_SPECTATE_DIALOG = new NiceAction("Spectate Room", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            openJoinDialog(true);
        }
    };
    private final NiceAction ACTION_PLAY_MATCH = new NiceAction("Play", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameHandler.join("matchme");
        }
    };
    private final NiceAction ACTION_START_GAME = new NiceAction("Start Game", KeyEvent.VK_SPACE, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameHandler.startGame();
        }
    };
    private final NiceAction ACTION_OPEN_DEBUG_DIALOG = new NiceAction("Open Debug Window", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            openDebugDialog();
        }
    };
    private JTextArea syncDataComp;

    private void openDebugDialog() {
        JFrame frame = new JFrame();
        frame.add(syncDataComp);
        frame.setSize(500, 200);
        frame.setVisible(true);
    }

    private GameMainPanel mainPanel;
    private final NiceAction ACTION_RESELECT_NODE = new NiceAction("Reselect Last Node", KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainPanel.selectLast();
        }
    };

    private final NiceAction ACTION_REATTACK_NODE = new NiceAction("Attack Last Node", KeyEvent.VK_T, KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainPanel.attackLast();
        }
    };
    private RoomInfoPanel roomPanel;

    public GameWindow(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        gameHandler.setSyncDataListener(this);
        setLayout(new BorderLayout());
        roomPanel = new RoomInfoPanel(this, gameHandler);
        add(roomPanel, BorderLayout.WEST);
        mainPanel = new GameMainPanel(gameHandler);
        add(mainPanel, BorderLayout.CENTER);
        syncDataComp = new JTextArea();
        syncDataComp.setEditable(false);
        syncDataComp.setLineWrap(true);
        new Thread(new RepaintThread(mainPanel)).start();
    }

    public static void open(GameHandler gameHandler) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GameWindow panel = new GameWindow(gameHandler);
        frame.add(panel);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(Math.min(1000, screenSize.width), Math.min(700, screenSize.height));
        frame.setJMenuBar(panel.getGameMenu());
        frame.setVisible(true);
    }

    private JMenuBar getGameMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu roomMenu = new JMenu("Room");
        roomMenu.setMnemonic(KeyEvent.VK_R);
        roomMenu.add(ACTION_OPEN_JOIN_DIALOG);
        roomMenu.add(ACTION_OPEN_SPECTATE_DIALOG);
        roomMenu.add(ACTION_PLAY_MATCH);
        roomMenu.add(ACTION_START_GAME);
        bar.add(roomMenu);
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);
        gameMenu.add(ACTION_START_GAME);
        gameMenu.add(ACTION_RESELECT_NODE);
        gameMenu.add(ACTION_REATTACK_NODE);
        gameMenu.add(ACTION_OPEN_DEBUG_DIALOG);
        bar.add(gameMenu);
        return bar;
    }

    private void updateActionState() {
        java.util.List<String> list = gameHandler.getUsers();
        if (list.isEmpty()) {
            ACTION_OPEN_JOIN_DIALOG.setName("Join Room");
            ACTION_START_GAME.setEnabled(false);
        } else {
            ACTION_OPEN_JOIN_DIALOG.setName("Switch Room");
            String leader = list.get(0);
            String userID = gameHandler.getUserID();
            ACTION_START_GAME.setEnabled(leader.equals(userID) && !gameHandler.hasGameData() && !gameHandler.isMatchMeRoom());
        }
    }


    public void openJoinDialog(boolean spectate) {
        String toJoin = JOptionPane.showInputDialog("Room to " + (spectate ? "spectate" : "join") + "?", "");
        if (toJoin != null) {
            if (spectate) {
                gameHandler.spectate(toJoin);
            } else gameHandler.join(toJoin);
        }
    }

    @Override
    public void onData(String data) {
        // receive sync data for debug window
        syncDataComp.setText(data);
    }

    private class RoomInfoPanel extends JPanel implements RoomInfoListener, ActionListener, AdjustmentListener {
        private JList<String> userList;
        private JList<ChatMessage> chatList;
        private JScrollPane chatPane;
        private JTextField chatMessage;
        private JButton joinBtn;
        private JButton startBtn;
        private JButton matchBtn;
        private JPanel topPanel;
        private JPanel bottomPanel;
        private GameHandler gameHandler;
        private GameWindow gameWindow;
        private int width = 150;

        public RoomInfoPanel(GameWindow gameWindow, GameHandler gameHandler) {
            this.gameWindow = gameWindow;
            this.gameHandler = gameHandler;
            gameHandler.setRoomInfoListener(this);
            setLayout(new BorderLayout());
            topPanel = new JPanel();
            topPanel.setLayout(new GridLayout(3, 1));
            joinBtn = new JButton(ACTION_OPEN_JOIN_DIALOG);
            topPanel.add(joinBtn);
            matchBtn = new JButton(ACTION_PLAY_MATCH);
            topPanel.add(matchBtn);
            startBtn = new JButton(ACTION_START_GAME);
            startBtn.setEnabled(false);
            topPanel.add(startBtn);
            add(topPanel, BorderLayout.NORTH);
            bottomPanel = new JPanel();
            bottomPanel.setLayout(new GridLayout(2, 1));
            userList = new JList<String>(new DefaultListModel<String>());
            userList.setCellRenderer(new UserListCellRenderer());
            bottomPanel.add(userList);
            chatList = new JList<ChatMessage>(new DefaultListModel<ChatMessage>());
            chatList.setCellRenderer(new ChatListCellRenderer());
            chatList.setFixedCellWidth(width);
            chatPane = new JScrollPane(chatList);
            chatPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            chatPane.getVerticalScrollBar().addAdjustmentListener(this);
            bottomPanel.add(chatPane);
            add(bottomPanel, BorderLayout.CENTER);
            chatMessage = new JTextField();
            chatMessage.addActionListener(this);
            add(chatMessage, BorderLayout.SOUTH);
            gameWindow.updateActionState();
        }

        public JTextField getChatField() {
            return chatMessage;
        }

        public void setChatEnabled(boolean enabled) {
            roomPanel.getChatField().setEnabled(enabled);
        }

        public void clearChat() {
            ((DefaultListModel<ChatMessage>) chatList.getModel()).clear();
        }

        @Override
        public void onChat(String user, String message) {
            ((DefaultListModel<ChatMessage>) chatList.getModel()).addElement(new ChatMessage(user, message));
        }

        @Override
        public void onError(String error) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            gameHandler.sendChat(chatMessage.getText());
            chatMessage.setText("");
        }

        @Override
        public void onLeftRoom(String id) {
            if (id.equals(gameHandler.getUserID())) {
                ((DefaultListModel<String>) userList.getModel()).removeAllElements();
                clearChat();
                setChatEnabled(false);
            } else {
                ((DefaultListModel<String>) userList.getModel()).removeElement(id);
            }
            gameWindow.updateActionState();
        }

        @Override
        public void onJoinedRoom(String id) {
            ((DefaultListModel<String>) userList.getModel()).addElement(id);
            gameWindow.updateActionState();
            clearChat();
            setChatEnabled(true);
        }

        @Override
        public void onGameStart() {
            gameWindow.updateActionState();
        }

        @Override
        public void onGameEnd() {
            gameWindow.updateActionState();
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
        }

        private class ChatListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JTextArea tr = new JTextArea();
                tr.setText(value.toString());
                tr.setLineWrap(true);
                tr.setWrapStyleWord(true);
                tr.setColumns(50);
                Dimension d = new Dimension(list.getFixedCellWidth(), tr.getPreferredSize().height);
                tr.setPreferredSize(d);
                tr.setSize(d);
                try {
                    Rectangle r = tr.modelToView(tr.getDocument().getLength());
                    tr.setPreferredSize(new Dimension(d.width, r.y + r.height));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                /*JLabel tr = new JLabel("<html><body style=\"width: "+(list.getFixedCellWidth()-50)+"px\">"+value.toString());*/
                Color userColor = Color.black;
                int userPosition = gameHandler.getPosition(((ChatMessage) value).getUser());
                if (userPosition != -1) {
                    userColor = MathHelper.getColorForOwner(userPosition);
                }
                tr.setForeground(userColor);
                return tr;
            }
        }

        private class UserListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component tr = super.getListCellRendererComponent(list, value, index, gameHandler.adjustForRemoved(index) == gameHandler.getPosition(), false);
                tr.setForeground(MathHelper.getColorForOwner(gameHandler.adjustForRemoved(index)));
                return tr;
            }
        }
    }

    private abstract class NiceAction extends AbstractAction {
        public NiceAction() {
            super();
        }

        public NiceAction(String name) {
            super(name);
        }

        public NiceAction(String name, int mnemonic) {
            this(name);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public NiceAction(String name, int mnemonic, KeyStroke accelerator) {
            this(name, mnemonic);
            putValue(ACCELERATOR_KEY, accelerator);
        }

        public void setName(String name) {
            putValue(NAME, name);
        }
    }
}

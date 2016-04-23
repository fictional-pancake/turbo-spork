package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GameWindow extends JPanel {

    private GameHandler gameHandler;
    private GameMainPanel mainPanel;

    private final NiceAction ACTION_OPEN_JOIN_DIALOG = new NiceAction("Switch Room", KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameHandler.openJoinDialog(false);
        }
    };

    private final NiceAction ACTION_OPEN_SPECTATE_DIALOG = new NiceAction("Spectate Room", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameHandler.openJoinDialog(true);
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

    private final NiceAction ACTION_OPEN_DEBUG_DIALOG = new NiceAction("Open Debug Window", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0)) {
        @Override
        public void actionPerformed(ActionEvent e) {
            gameHandler.openDebugDialog();
        }
    };

    public GameWindow(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        setLayout(new BorderLayout());
        RoomInfoPanel leftPanel = new RoomInfoPanel(this, gameHandler);
        add(leftPanel, BorderLayout.WEST);
        mainPanel = new GameMainPanel(gameHandler);
        add(mainPanel, BorderLayout.CENTER);
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
            ACTION_START_GAME.setEnabled(leader.equals(userID) && !gameHandler.isInProgress() && !gameHandler.isMatchMeRoom());
        }
    }

    private class RoomInfoPanel extends JPanel implements RoomInfoListener {
        private JList<String> userList;
        private JButton joinBtn;
        private JButton startBtn;
        private JButton matchBtn;
        private JPanel topPanel;
        private GameHandler gameHandler;
        private GameWindow gameWindow;

        public RoomInfoPanel(GameWindow gameWindow, GameHandler gameHandler) {
            this.gameWindow = gameWindow;
            this.gameHandler = gameHandler;
            gameHandler.setRoomInfoListener(this);
            setLayout(new BorderLayout());
            userList = new JList<String>(new DefaultListModel<String>());
            userList.setCellRenderer(new UserListCellRenderer());
            add(userList, BorderLayout.CENTER);
            topPanel = new JPanel();
            topPanel.setLayout(new GridLayout(2, 1));
            joinBtn = new JButton(ACTION_OPEN_JOIN_DIALOG);
            matchBtn = new JButton(ACTION_PLAY_MATCH);
            topPanel.add(joinBtn);
            topPanel.add(matchBtn);
            add(topPanel, BorderLayout.NORTH);
            startBtn = new JButton(ACTION_START_GAME);
            startBtn.setEnabled(false);
            add(startBtn, BorderLayout.SOUTH);
            gameWindow.updateActionState();
        }

        @Override
        public void onLeftRoom(String id) {
            if (id.equals(gameHandler.getUserID())) {
                ((DefaultListModel<String>) userList.getModel()).removeAllElements();
            } else {
                ((DefaultListModel<String>) userList.getModel()).removeElement(id);
            }
            gameWindow.updateActionState();
        }

        @Override
        public void onJoinedRoom(String id) {
            ((DefaultListModel<String>) userList.getModel()).addElement(id);
            gameWindow.updateActionState();
        }

        @Override
        public void onGameStart() {
            gameWindow.updateActionState();
        }

        @Override
        public void onGameEnd() {
            gameWindow.updateActionState();
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

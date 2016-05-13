package fictionalpancake.turbospork;

import fictionalpancake.turbospork.gui.GameWindow;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.util.*;

public class GameHandler extends WebSocketClient {

    private boolean gameStarted;
    private DataListener<String> firstMessageListener;
    private RoomInfoListener roomInfoListener;
    private String newRoom;
    private String room;
    private String userID;
    private List<Node> nodes;
    private String lastWinner;
    private boolean opened;
    private List<UnitGroup> groups;
    private List<String> users;
    private List<Integer> removed;
    private DataListener<String> syncDataListener;

    private JSONParser jsonParser = new JSONParser();

    public GameHandler(DataListener<String> firstMessageListener, URI uri) {
        super(uri);
        gameStarted = false;
        this.firstMessageListener = firstMessageListener;
        users = new ArrayList<String>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (userID != null) {
                        send("keepalive");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        opened = true;
    }

    @Override
    public void onMessage(String s) {
        if (firstMessageListener != null) {
            firstMessageListener.onData(s);

            // it should only be used once
            firstMessageListener = null;
        }
        int ind = s.indexOf(':');
        String command;
        String data = null;
        if (ind > -1) {
            command = s.substring(0, ind);
            data = s.substring(ind + 1);
        } else {
            command = s;
        }
        handleMessage(command, data);
    }

    public void handleMessage(String command, String data) {
        String[] spl = new String[]{};
        if (data != null) {
            spl = data.split(",");
        }
        switch (command) {
            case "leave":
                if (data.equals(userID)) {
                    room = null;
                    users.clear();
                    reset();
                } else {
                    if (hasGameData()) {
                        removed.add(users.indexOf(data));
                    }
                    users.remove(data);
                }
                if (roomInfoListener != null) {
                    roomInfoListener.onLeftRoom(data);
                }
                break;
            case "join":
                if (userID == null) {
                    userID = data;
                } else {
                    if (newRoom != null) {
                        room = newRoom;
                        newRoom = null;
                    }
                    lastWinner = null;
                    users.add(data);
                    if (roomInfoListener != null) {
                        roomInfoListener.onJoinedRoom(data);
                    }
                }
                break;
            case "error":
                if (userID != null && roomInfoListener != null) {
                    roomInfoListener.onError(data);
                }
                break;
            case "gameinfo":
                try {
                    Map map = (Map) jsonParser.parse(data);
                    List<Map> nodeInfo = (List<Map>) map.get("nodes");
                    nodes = new ArrayList<Node>();
                    groups = new ArrayList<UnitGroup>();
                    removed = new ArrayList<Integer>();
                    for (Map currentNodeInfo : nodeInfo) {
                        nodes.add(new Node(currentNodeInfo));
                    }
                    if (roomInfoListener != null) {
                        roomInfoListener.onGameStart();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "gamestart":
                gameStarted = true;
                break;
            case "send":
                try {
                    Map map = (Map) jsonParser.parse(data);
                    UnitGroup newGroup = new UnitGroup(map, this);
                    synchronized(groups) {
                        groups.add(newGroup);
                    }
                    int taken = 0;
                    int toTake = newGroup.getUnits();
                    taken += newGroup.getSource().takeUnits(newGroup.getOwner(), toTake);
                    if (taken < toTake) {
                        System.out.println("Warning: only took " + taken + "/" + toTake);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "win":
                lastWinner = data;
                reset();
                if (roomInfoListener != null) {
                    roomInfoListener.onGameEnd();
                }
                break;
            case "update":
                getNodes().get(Integer.parseInt(spl[0])).updateProp(spl[1], spl[2]);
                break;
            case "death":
                Node node = getNodes().get(Integer.parseInt(spl[0]));
                int owner = Integer.parseInt(spl[1]);
                node.takeUnits(owner, 1);
                break;
            case "chat":
                int colon = data.indexOf(":");
                String user = data.substring(0, colon);
                String message = data.substring(colon + 1);
                roomInfoListener.onChat(user, message);
                break;
            case "sync":
                try {
                    if (syncDataListener != null) {
                        syncDataListener.onData(data);
                    }
                    Map map = (Map) jsonParser.parse(data);
                    List<Map> nodeData = ((List<Map>) map.get("nodes"));
                    List<Node> nodes = getNodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        Map curNodeData = nodeData.get(i);
                        Node curNode = nodes.get(i);
                        curNode.sync(curNodeData);
                    }
                    Map<String, Long> groupMap = ((Map<String, Long>) map.get("groups"));
                    List<UnitGroup> groups = getUnitGroups();
                    synchronized(groups) {
                        Iterator<UnitGroup> it = groups.iterator();
                        while (it.hasNext()) {
                            UnitGroup group = it.next();
                            String id = group.getID() + "";
                            if (groupMap.containsKey(id)) {
                                group.setUnits(MathHelper.toInt(groupMap.get(id)));
                            } else {
                                it.remove();
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.err.println("Unrecognized command");
        }
    }

    private void reset() {
        nodes = null;
        groups = null;
        removed = null;
        gameStarted = false;
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if (opened && userID != null) {
            System.err.println("dying");
            System.exit(0);
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void join(String s) {
        newRoom = s;
        send("join:" + newRoom);
    }

    public void sendChat(String message) {
        send("chat:" + message);
    }

    public String getUserID() {
        return userID;
    }

    public void setRoomInfoListener(RoomInfoListener roomInfoListener) {
        this.roomInfoListener = roomInfoListener;
    }

    public void startGame() {
        send("gamestart");
    }

    public boolean isInProgress() {
        return gameStarted;
    }

    public boolean hasGameData() {
        return nodes != null;
    }

    public List<Node> getNodes() {
        if (groups != null) {
            synchronized(groups) {
                Iterator<UnitGroup> it = groups.iterator();
                while (it.hasNext()) {
                    UnitGroup group = it.next();
                    if (group.isComplete()) {
                        it.remove();
                        group.getDest().addUnits(group.getOwner(), group.getUnits());
                    }
                }
            }
        }
        return nodes;
    }

    public String getLastWinner() {
        return lastWinner;
    }

    public int getPosition(String username) {
        return adjustForRemoved(users.indexOf(username));
    }

    public int getPosition() {
        return getPosition(userID);
    }

    public int adjustForRemoved(int i) {
        int tr = i;
        if (removed != null) {
            Collections.sort(removed);
            for (int j : removed) {
                if (j <= tr) {
                    tr--;
                }
            }
        }
        return tr;
    }

    public void attack(Node target, Node with) {
        send("attack:" + indexOf(with) + "," + indexOf(target));
    }

    public int indexOf(Node node) {
        if (nodes == null) {
            return -1;
        }
        return getNodes().indexOf(node);
    }

    public List<UnitGroup> getUnitGroups() {
        return groups;
    }

    public boolean isMatchMeRoom() {
        return room != null && room.indexOf("matchme") == 0;
    }

    public List<String> getUsers() {
        return users;
    }

    public boolean isSpectating() {
        return !getUsers().isEmpty() && getPosition() == -1;
    }

    public void spectate(String s) {
        send("spectate:" + s);
    }

    public void setSyncDataListener(DataListener<String> syncDataListener) {
        this.syncDataListener = syncDataListener;
    }
}

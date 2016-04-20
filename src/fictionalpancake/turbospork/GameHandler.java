package fictionalpancake.turbospork;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.net.URI;
import java.util.*;

public class GameHandler extends WebSocketClient {

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
    private JTextArea syncDataComp;

    private JSONParser jsonParser = new JSONParser();

    public GameHandler(DataListener<String> firstMessageListener, URI uri) {
        super(uri);
        this.firstMessageListener = firstMessageListener;
        users = new ArrayList<String>();
        syncDataComp = new JTextArea();
        syncDataComp.setEditable(false);
        syncDataComp.setLineWrap(true);

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
        String[] spl = data.split(",");
        switch (command) {
            case "leave":
                if (data.equals(userID)) {
                    room = null;
                    users.clear();
                    reset();
                } else {
                    if (isInProgress()) {
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
                if (userID != null) {
                    JOptionPane.showMessageDialog(null, data, "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "gamestart":
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
            case "send":
                try {
                    Map map = (Map) jsonParser.parse(data);
                    UnitGroup newGroup = new UnitGroup(map, this);
                    groups.add(newGroup);
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
            case "sync":
                try {
                    syncDataComp.setText(data);
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
                    Iterator<UnitGroup> it = groups.iterator();
                    while (it.hasNext()) {
                        UnitGroup group = it.next();
                        String id = group.getID() + "";
                        if (groupMap.containsKey(id)) {
                            group.setUnits(TurboSpork.toInt(groupMap.get(id)));
                        } else {
                            it.remove();
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

    public void openJoinDialog(boolean spectate) {
        if (firstMessageListener == null) {
            String toJoin = JOptionPane.showInputDialog("Room to " + (spectate ? "spectate" : "join") + "?", "");
            if (toJoin != null) {
                if (spectate) {
                    send("spectate:"+toJoin);
                } else join(toJoin);
            } else {
                send("leave");
            }
        }
    }

    public void join(String s) {
        newRoom = s;
        send("join:" + newRoom);
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
        return nodes != null;
    }

    public List<Node> getNodes() {
        if (groups != null) {
            Iterator<UnitGroup> it = groups.iterator();
            while (it.hasNext()) {
                UnitGroup group = it.next();
                if (group.isComplete()) {
                    it.remove();
                    group.getDest().addUnits(group.getOwner(), group.getUnits());
                }
            }
        }
        return nodes;
    }

    public String getLastWinner() {
        return lastWinner;
    }

    public int getPosition() {
        return adjustForRemoved(users.indexOf(userID));
    }

    int adjustForRemoved(int i) {
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

    public void openDebugDialog() {
        JFrame dialog = new JFrame();
        dialog.add(syncDataComp);
        dialog.setSize(500, 200);
        dialog.setVisible(true);
    }

    public boolean isSpectating() {
        return !getUsers().isEmpty() && getPosition() == -1;
    }
}

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

    private JSONParser jsonParser = new JSONParser();

    public GameHandler(DataListener<String> firstMessageListener, URI uri) {
        super(uri);
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
                    if (newGroup.getOwner() == newGroup.getSource().getOwner()) {
                        taken += newGroup.getSource().takeUnits(toTake, this);
                    }
                    for (int i = 0; i < groups.size() && taken < toTake; i++) {
                        UnitGroup group = groups.get(i);
                        if (group.getDest() == newGroup.getSource() && group.getOwner() == newGroup.getOwner()) {
                            taken += group.takeUnits(toTake - taken);
                        }
                    }
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
                if (node.getOwner() == owner) {
                    node.takeUnits(1, this);
                } else {
                    for (UnitGroup group : groups) {
                        if (group.getOwner() == owner) {
                            group.takeUnits(1);
                            break;
                        }
                    }
                }
                break;
            case "sync":
                for (int i = 0; i < nodes.size(); i++) {
                    String c = spl[i];
                    String[] curData = c.split("/");
                    Node curNode = getNodes().get(i);
                    curNode.setOwner(Integer.parseInt(curData[0]));
                    if (curNode.getOwner() != -1) {
                        curNode.setUnits(Integer.parseInt(curData[1]));
                    }
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

    public void openJoinDialog() {
        if (firstMessageListener == null) {
            join(JOptionPane.showInputDialog("Room to join?", ""));
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
                    if (group.getDest().getOwner() == group.getSource().getOwner() || group.getUnits() < 1){
                        it.remove();
                        group.getDest().addUnits(group.getUnits());
                    }
                    else {
                        boolean pastMe = false;
                        for(int i = 0; i < groups.size(); i++) {
                            UnitGroup other = groups.get(i);
                            if(other == group) {
                                pastMe = true;
                            }
                            else if(pastMe && other.isComplete() && other.getDest() == group.getDest() && other.getOwner() == group.getOwner()) {
                                other.addUnits(group.getUnits());
                                it.remove();
                                break;
                            }
                        }
                    }
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
}

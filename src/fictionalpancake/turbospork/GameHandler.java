package fictionalpancake.turbospork;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    private JSONParser jsonParser = new JSONParser();

    public GameHandler(DataListener<String> firstMessageListener) {
        super(TurboSpork.getServerURI());
        this.firstMessageListener = firstMessageListener;
        users = new ArrayList<String>();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("opened");
        opened = true;
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
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
                    users.add(data);
                    if (roomInfoListener != null) {
                        roomInfoListener.onJoinedRoom(data);
                    }
                }
                break;
            case "error":
                JOptionPane.showMessageDialog(null, data, "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "gamestart":
                try {
                    Map map = (Map) jsonParser.parse(data);
                    List<Map> nodeInfo = (List<Map>) map.get("nodes");
                    nodes = new ArrayList<Node>();
                    groups = new ArrayList<UnitGroup>();
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
                    groups.add(new UnitGroup(map, this));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "win":
                lastWinner = data;
                reset();
                if(roomInfoListener != null) {
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
                    node.takeUnits(1);
                } else {
                    for (UnitGroup group : groups) {
                        if (group.getSource().getOwner() == owner) {
                            group.takeUnits(1);
                            break;
                        }
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
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if (opened) {
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
            newRoom = JOptionPane.showInputDialog("Room to join?", "");
            send("join:" + newRoom);
        }
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
                if ((group.isComplete() && (group.getDest().getOwner() == group.getSource().getOwner() || group.getDest().getOwner() == -1) || group.getUnits() < 1)) {
                    it.remove();
                    group.getDest().addUnits(group.getUnits());
                    System.out.println("group dying");
                }
            }
        }
        return nodes;
    }

    public String getLastWinner() {
        return lastWinner;
    }

    public int getPosition() {
        return users.indexOf(userID);
    }

    public void attack(Node target, Node with) {
        int units = with.takeUnits();
        send("attack:" + indexOf(with) + "," + indexOf(target));
    }

    public int indexOf(Node node) {
        if(nodes == null) {
            return -1;
        }
        return getNodes().indexOf(node);
    }

    public List<UnitGroup> getUnitGroups() {
        return groups;
    }
}

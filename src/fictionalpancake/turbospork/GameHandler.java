package fictionalpancake.turbospork;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.util.ArrayList;
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

    private JSONParser jsonParser = new JSONParser();

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
        switch (command) {
            case "leave":
                if (data.equals(userID)) {
                    room = null;
                }
                if (roomInfoListener != null) {
                    roomInfoListener.onLeftRoom(data);
                }
                break;
            case "join":
                if (newRoom == null && room == null) {
                    userID = data;
                } else {
                    room = newRoom;
                    newRoom = null;
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
                    for(Map currentNodeInfo : nodeInfo) {
                        nodes.add(new Node(currentNodeInfo));
                    }
                    if(roomInfoListener != null) {
                        roomInfoListener.onGameStart();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "win":
                lastWinner = data;
                nodes = null;
                break;
            default:
                System.err.println("Unrecognized command");
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if(opened) {
            System.exit(0);
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public GameHandler(DataListener<String> firstMessageListener) {
        super(TurboSpork.getServerURI());
        this.firstMessageListener = firstMessageListener;
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
        return nodes;
    }

    public String getLastWinner() {
        return lastWinner;
    }
}

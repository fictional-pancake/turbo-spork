package fictionalpancake.turbospork;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class GameHandler extends WebSocketClient {

    private DataListener<String> firstMessageListener;

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("opened");
    }

    @Override
    public void onMessage(String s) {
        if(firstMessageListener != null) {
            firstMessageListener.onData(s);

            // it should only be used once
            firstMessageListener = null;
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public GameHandler(DataListener<String> firstMessageListener) {
        super(TurboSpork.getServerURI());
        this.firstMessageListener = firstMessageListener;
    }
}

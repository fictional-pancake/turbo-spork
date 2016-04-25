package fictionalpancake.turbospork;

public interface RoomInfoListener {
    void onLeftRoom(String id);

    void onJoinedRoom(String id);

    void onGameStart();

    void onGameEnd();

    void onChat(String user, String message);
}

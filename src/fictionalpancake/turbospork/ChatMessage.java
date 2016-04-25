package fictionalpancake.turbospork;

public class ChatMessage {
    String user;
    String message;

    public ChatMessage(String ownerName, String message) {
        this.user = ownerName;
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getUser() + ": " + getMessage();
    }
}

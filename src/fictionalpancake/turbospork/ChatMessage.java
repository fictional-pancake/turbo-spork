package fictionalpancake.turbospork;

public class ChatMessage {
    private String user;
    private String message;

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

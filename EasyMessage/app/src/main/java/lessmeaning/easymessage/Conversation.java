package lessmeaning.easymessage;

/**
 * Created by пользователь on 11.11.2016.
 */

public class Conversation {

    private String friend;
    private long conversationID;
    private long time;

    private Row row;

    public Conversation(long conversationID, String friend, long time) {
        this.conversationID = conversationID;
        this.friend = friend;
        this.time = time;
    }

    public void setLastRow(Row lastRow) { this.row = lastRow; }

    public long getTime() { return time; }

    public long getConversationID() { return conversationID; }

    public String getFriend() { return friend; }

    public Row getRow() {
        return row;
    }
}

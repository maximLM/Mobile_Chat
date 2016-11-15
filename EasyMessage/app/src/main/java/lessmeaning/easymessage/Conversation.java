package lessmeaning.easymessage;

/**
 * Created by пользователь on 11.11.2016.
 */

public class Conversation implements Comparable<Conversation> {

    private String friend;
    private long conversationID;
    private long time;
    private String lastRow;

    public Conversation(long conversationID, String friend, long time) {
        this.conversationID = conversationID;
        this.friend = friend;
        this.time = time;
        this.lastRow = lastRow;
    }

    public String getLastRow() { return lastRow; }

    public void setLastRow(String lastRow) { this.lastRow = lastRow; }

    public long getTime() { return time; }

    public long getConversationID() { return conversationID; }

    public String getFriend() { return friend; }


    @Override
    public int compareTo(Conversation conv) {
        return (int) (time - conv.time);
    }
}

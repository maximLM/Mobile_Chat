package lessmeaning.easymessage;

/**
 * Created by пользователь on 11.11.2016.
 */

public class Conversation implements Comparable<Conversation> {

    private String friend;
    private long conversationID;
    private long time;

    private Row row;

    public Conversation(long conversationID, String friend, long time) {
        this.conversationID = conversationID;
        this.friend = friend;
        this.time = time;
    }

    public void setLastRow(Row lastRow) {
        this.row = new Row(1,"1","2",1);
    }

    public long getTime() { return time; }

    public long getConversationID() { return conversationID; }

    public String getFriend() { return friend; }


    @Override
    public int compareTo(Conversation conv) {
        return (int) (time - conv.time);
    }

    public Row getRow() {
        return row;
    }
}

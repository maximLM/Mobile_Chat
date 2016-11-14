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
        row = new Row(conversationID, friend, "sooqa", time);
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

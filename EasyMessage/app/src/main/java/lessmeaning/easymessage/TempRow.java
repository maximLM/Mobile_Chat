package lessmeaning.easymessage;

/**
 * Created by пользователь on 11.11.2016.
 */

public class TempRow {
    private long conversationID;
    private String content;

    public TempRow(long conversationID, String content) {
        this.conversationID = conversationID;
        this.content = content;
    }

    public long getConversationID() { return conversationID; }

    public String getContent() { return content; }
}

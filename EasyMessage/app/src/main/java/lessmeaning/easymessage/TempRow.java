package lessmeaning.easymessage;

/**
 * Created by пользователь on 11.11.2016.
 */

public class TempRow {
    private long conversationID;
    private String content;
    private long id;

    public TempRow(long conversationID, String content, long id) {
        this.conversationID = conversationID;
        this.content = content;
        this.id = id;
    }

    public long getConversationID() { return conversationID; }

    public String getContent() { return content; }

    public long getId() { return id; }
}

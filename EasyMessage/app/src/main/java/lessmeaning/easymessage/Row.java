package lessmeaning.easymessage;

import android.util.Log;

/**
 * Created by Максим on 03.11.2016.
 */
class Row implements Comparable<Row> {
    private String content;
    private long time;
    private long conversationID;
    private String userSender;

    public Row(long conversationID, String userSender, String content, long time) {
        this.content = content;
        this.time = time;
        this.conversationID = conversationID;
        this.userSender = userSender;
    }

    @Override
    public int compareTo(Row row) {
        return (int) (time - row.time);
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }


    public long getConversationID() { return conversationID; }

    public String getUserSender() { return userSender; }

    public void decrypt() {
        content = MessageEncryption.decrypt(content, conversationID);
    }

    @Override
    protected Row clone() {
        return new Row(conversationID, userSender, content, time);
    }
}

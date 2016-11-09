package lessmeaning.easymessage;

/**
 * Created by Максим on 03.11.2016.
 */
class Row implements Comparable<Row>{
    private String content;
    private long time;
    private long conversationID;
    private String userSender;
    public Row(long conversationID, String userSender, String content, long time) {
        this.conversationID = conversationID;
        this.userSender = userSender;
        this.content = content;
        this.time = time;
    }

    @Override
    public int compareTo(Row row) {
        return (int) (row.time - time);
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public long getConversationID() {return conversationID;}

    public String getUserSender() {return  userSender;}
}
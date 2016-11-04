package lessmeaning.easymessage;

/**
 * Created by Максим on 03.11.2016.
 */
class Row implements Comparable<Row>{
    String content;
    long time;
    public Row(String content, long time) {
        this.content = content;
        this.time = time;
    }

    @Override
    public int compareTo(Row row) {
        return (int) (time - row.time);
    }
}

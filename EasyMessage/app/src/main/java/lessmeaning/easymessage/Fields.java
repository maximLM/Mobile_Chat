package lessmeaning.easymessage;

/**
 * Created by Максим on 05.11.2016.
 */
public enum  Fields {

    ACTION("action"), AUTHOR("author"), CONVERSATION("conversation"),
    TIME("time"), MESSAGE("content"), SUCCESS("success");

    private String name;

    Fields(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}

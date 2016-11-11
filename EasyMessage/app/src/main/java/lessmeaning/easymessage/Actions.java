package lessmeaning.easymessage;

/**
 * Created by Максим on 05.11.2016.
 */
public enum Actions {

    SEND("send"), GET("get");
    private String name;

    Actions(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

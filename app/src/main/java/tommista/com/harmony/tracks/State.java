package tommista.com.harmony.tracks;

public enum State {
    CREATED("Created"),
    PREPARING("Preparing"),
    PLAYING("Playing"),
    PAUSED("Paused"),
    DESTROYED("Destroyed");

    String value;

    State(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
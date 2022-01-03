package Ultis;

public enum State {

    NEW_USER(1),
    IN_LOBBY(2),
    IN_CHAT(3);

    private int id;
    State(int id ) {this.id = id; }

    public int getID() {return this.id;}
}

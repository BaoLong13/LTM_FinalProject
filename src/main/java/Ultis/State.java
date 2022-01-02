package Ultis;

public enum State {

    IN_LOBBY(1),
    IN_CHAT(2);

    private int id;
    State(int id ) {this.id = id; }

    public int getID() {return this.id;}
}

package Ultility;

public enum State {

    NEW_USER(1),
    IN_LOBBY(2),
    MID_INVITE(3),
    IN_CHAT(4);

    private int id;
    State(int id ) {this.id = id; }

    public int getID() {return this.id;}
}

package core;

public class Room {
    public String name;
    public Mob[] mobs;
    public String description;
    public String exits;
    public Room(String name, String description, String exits) {
        this.name = name;
        this.description = description;
        this.exits = exits;
    }
    protected void setRoomMobs(Mob[] mobs) {
        this.mobs = mobs;
    }
    protected void setRoomExits(String exits) {
        this.exits = exits;
    }
    protected void setRoomDescription(String description) {
        this.description = description;
    }
    protected void setRoomName(String name) {
        this.name = name;
    }
}

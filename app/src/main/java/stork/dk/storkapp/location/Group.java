package stork.dk.storkapp.location;

import java.util.List;

/**
 * @author Morten Erfurt Hansen
 */
public class Group implements Traceable {
    private int id;
    private String name;
    private List<Friend> friends;

    public Group(int id, String name, List<Friend> friends) {
        this.id = id;
        this.name = name;
        this.friends = friends;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }
}

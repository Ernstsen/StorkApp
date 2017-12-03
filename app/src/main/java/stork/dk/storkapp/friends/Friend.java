package stork.dk.storkapp.friends;

import stork.dk.storkapp.communicationObjects.helperObjects.Location;

/**
 * @author Morten Erfurt Hansen
 */
public class Friend implements Traceable {
    private int id;
    private String name;
    private Location location;

    public Friend(int id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return "   " + name;
    }
}

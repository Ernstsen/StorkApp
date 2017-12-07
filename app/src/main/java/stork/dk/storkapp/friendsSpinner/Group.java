package stork.dk.storkapp.friendsSpinner;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * @author morten
 */
public class Group implements Traceable, Serializable, Comparable {
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

    @Override
    public int compareTo(@NonNull Object o) {
        Group obj = (Group) o;
        return name.compareTo(obj.getName());
    }
}

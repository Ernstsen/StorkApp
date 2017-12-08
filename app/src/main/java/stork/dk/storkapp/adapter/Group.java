package stork.dk.storkapp.adapter;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * @author morten, johannes
 */
public class Group implements Traceable, Serializable, Comparable {
    private int id;
    private String name;
    private List<Friend> friends;
    private int owner;
    private int active = 1;

    public Group(int id, String name, List<Friend> friends, int owner, int active) {
        this.id = id;
        this.name = name;
        this.friends = friends;
        this.owner = owner;
        this.active = active;
    }

    public Group(String name, List<Friend> friends) {
        this.id = 0;
        this.name = name;
        this.friends = friends;
        this.owner = 0;
        this.active = 1;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner( int owner) {
        this.owner = owner;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
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

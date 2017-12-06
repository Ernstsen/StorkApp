package stork.dk.storkapp.communicationObjects;

import android.support.annotation.NonNull;

import java.util.Collection;

/**
 * @author Johannes Ernstsen
 */
public class PublicUserObject implements Comparable {
    private int userId;
    private String name;
    private String mail;

    public PublicUserObject() {
    }

    public PublicUserObject(int userId, String name, String mail) {
        this.userId = userId;
        this.name = name;
        this.mail = mail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        PublicUserObject obj = (PublicUserObject) o;
        return name.compareTo(obj.getName());
    }
}

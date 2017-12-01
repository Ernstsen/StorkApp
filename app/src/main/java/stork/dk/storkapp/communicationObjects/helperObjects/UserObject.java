package stork.dk.storkapp.communicationObjects.helperObjects;

import java.io.Serializable;

/**
 * @author Johannes on 30-11-2017.
 */

public class UserObject implements Serializable {
    private int id;
    private String name;
    private String mail;
    private String sessionId;

    public UserObject() {
    }

    public UserObject(int id, String name, String mail, String sessionId) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.sessionId = sessionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}

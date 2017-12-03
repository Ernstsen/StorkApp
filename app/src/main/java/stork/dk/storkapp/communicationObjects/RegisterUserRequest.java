package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;

/**
 * @author Johannes Ernstsen
 */
public class RegisterUserRequest implements Serializable {
    private String name;
    private String password;
    private String mail;
    private int userId;
    private String sessionId;

    public RegisterUserRequest() {
    }

    public RegisterUserRequest(String name, String password, String mail, int userId, String sessionId) {
        this.name = name;
        this.password = password;
        this.mail = mail;
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

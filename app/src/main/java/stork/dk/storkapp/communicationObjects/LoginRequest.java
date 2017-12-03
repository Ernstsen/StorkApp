package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;

/**
 * @author Johannes on 30-11-2017.
 */
public class LoginRequest implements Serializable {
    private String sessionId;
    private String mail;
    private String password;
    private int userId;

    public LoginRequest() {
    }

    public LoginRequest(String sessionId, String mail, String password, int userId) {
        this.sessionId = sessionId;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
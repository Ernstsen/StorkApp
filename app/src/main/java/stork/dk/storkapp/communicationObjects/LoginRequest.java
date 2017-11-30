package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;

/**
 * @author Johannes on 30-11-2017.
 */

public class LoginRequest implements Serializable {
    private boolean success;
    private String sessionId;
    private String mail;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(boolean success, String sessionId, String mail, String password) {
        this.success = success;
        this.sessionId = sessionId;
        this.mail = mail;
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

}

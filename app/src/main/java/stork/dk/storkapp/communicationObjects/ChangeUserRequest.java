package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;

/**
 *@author Mathias
 */
public class ChangeUserRequest implements Serializable {
    private String name;
    private int userId;
    private String sessionId;
    private String password;
    private String newPassword;

    public ChangeUserRequest() {
    }

    public ChangeUserRequest(String name, int userId, String sessionId, String password, String newPassword) {
        this.name = name;
        this.userId = userId;
        this.sessionId = sessionId;
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() { return newPassword;}

    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}

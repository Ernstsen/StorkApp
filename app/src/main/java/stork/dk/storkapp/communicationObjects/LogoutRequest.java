package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;

/**
 * @author Johannes on 30-11-2017.
 */

public class LogoutRequest implements Serializable {

    private int userId;
    private String sessionId;
    private boolean success;

    public LogoutRequest() {
    }

    public LogoutRequest(int userId, String sessionId, boolean success) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.success = success;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}

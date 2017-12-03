package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;
import java.util.List;

/**
 * @author Johannes Ernstsen
 */
public class FriendChangeRequest implements Serializable {
    private ActionEnum action;
    private int userId;
    private String sessionId;
    private List<Integer> friends;

    public FriendChangeRequest() {
    }

    public FriendChangeRequest(ActionEnum action, int userId, String sessionId, List<Integer> friends) {
        this.action = action;
        this.userId = userId;
        this.sessionId = sessionId;
        this.friends = friends;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
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

    public List<Integer> getFriends() {
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
    }

    public enum ActionEnum {
        ADD, REMOVE
    }
}

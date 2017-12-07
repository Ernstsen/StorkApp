package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mathiasjensen on 07/12/17.
 */
public class GroupChangeActivationRequest implements Serializable {
    private int userId;
    private String sessionId;
    private List<Integer> add;
    private List<Integer> remove;

    public GroupChangeActivationRequest() {
    }

    public GroupChangeActivationRequest(int userId, String sessionId, List<Integer> add, List<Integer> remove) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.add = add;
        this.remove = remove;
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

    public List<Integer> getActivateGroup() {
        return add;
    }

    public void setActivateGroup(List<Integer> add) {
        this.add = add;
    }

    public List<Integer> getDeactivateGroup() {
        return remove;
    }

    public void setDeactivateGroup(List<Integer> remove) {
        this.remove = remove;
    }
}

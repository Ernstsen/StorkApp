package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;

import stork.dk.storkapp.communicationObjects.helperObjects.Location;

/**
 * @author Johannes on 30-11-2017.
 */

public class UpdateLocationRequest implements Serializable {
    private int userId;
    private String sessionId;
    private Location location;

    public UpdateLocationRequest() {
    }

    public UpdateLocationRequest(int userID, String sessionId, Location location) {
        this.userId = userID;
        this.sessionId = sessionId;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}

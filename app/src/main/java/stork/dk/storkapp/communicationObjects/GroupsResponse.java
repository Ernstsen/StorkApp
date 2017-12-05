package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import stork.dk.storkapp.communicationObjects.helperObjects.Location;
import stork.dk.storkapp.friendsSpinner.Group;

/**
 * @author Johannes, Morten
 */

public class GroupsResponse implements Serializable {
    private Map<String, List<Group>> groups;

    public GroupsResponse() {
    }

    public GroupsResponse(Map<String, List<Group>> groups) {
        this.groups = groups;
    }

    public Map<String, List<Group>> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, List<Group>> groups) {
        this.groups = groups;
    }
}
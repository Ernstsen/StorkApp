package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;
import java.util.List;

import stork.dk.storkapp.adapter.Group;

/**
 * @author Johannes, Morten
 */

public class GroupsResponse implements Serializable {
    private List<Group> groups;

    public GroupsResponse() {
    }

    public GroupsResponse(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
package stork.dk.storkapp.communicationObjects;

import java.util.List;

/**
 * @author Johannes Ernstsen
 */
public class UsersResponse {
    private List<PublicUserObject> users;

    @SuppressWarnings("unused")
    public UsersResponse() {
    }

    @SuppressWarnings("unused")
    public UsersResponse(List<PublicUserObject> users) {
        this.users = users;
    }

    @SuppressWarnings("unused")
    public List<PublicUserObject> getUsers() {
        return users;
    }

    @SuppressWarnings("unused")
    public void setUsers(List<PublicUserObject> users) {
        this.users = users;
    }
}

package stork.dk.storkapp.communicationObjects;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Johannes Ernstsen
 */
public class ChangeGroupRequest {
    /**
     * unique group Id - 0 if group is to be created
     */
    private int id;

    /**
     * name of group. Cannot be null when group is created.
     */
    private String name;

    /**
     * owner id
     */
    @NotNull
    private int userId;

    /**
     * owner sessionId
     */
    @NotNull
    private String sessionId;

    /**
     * id's of users to be added
     */
    @Nullable
    private List<Integer> add;

    /**
     * id's of users to be removed
     */
    @Nullable
    private List<Integer> remove;

    @SuppressWarnings("unused")
    public ChangeGroupRequest() {
    }

    @SuppressWarnings("unused")
    public ChangeGroupRequest(int id,
                              String name,
                              int userId,
                              String sessionId,
                              List<Integer> add,
                              List<Integer> remove) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.sessionId = sessionId;
        this.add = add;
        this.remove = remove;
    }

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public int getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @SuppressWarnings("unused")
    public String getSessionId() {
        return sessionId;
    }

    @SuppressWarnings("unused")
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @SuppressWarnings("unused")
    public List<Integer> getAdd() {
        return add;
    }

    @SuppressWarnings("unused")
    public void setAdd(List<Integer> add) {
        this.add = add;
    }

    @SuppressWarnings("unused")
    public List<Integer> getRemove() {
        return remove;
    }

    @SuppressWarnings("unused")
    public void setRemove(List<Integer> remove) {
        this.remove = remove;
    }
}

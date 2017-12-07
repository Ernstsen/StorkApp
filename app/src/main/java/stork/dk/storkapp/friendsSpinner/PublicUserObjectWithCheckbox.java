package stork.dk.storkapp.friendsSpinner;

import android.support.annotation.NonNull;

import stork.dk.storkapp.communicationObjects.PublicUserObject;

/**
 * @author morten
 */

public class PublicUserObjectWithCheckbox extends PublicUserObject implements Comparable {
    private boolean checked;

    public PublicUserObjectWithCheckbox(int userId, String name, String mail, boolean checked) {
        super(userId, name, mail);
        this.checked = checked;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        PublicUserObjectWithCheckbox otherUserObject = (PublicUserObjectWithCheckbox) other;

        return super.getUserId() == otherUserObject.getUserId();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        PublicUserObjectWithCheckbox obj = (PublicUserObjectWithCheckbox) o;
        return getName().compareTo(obj.getName());
    }
}

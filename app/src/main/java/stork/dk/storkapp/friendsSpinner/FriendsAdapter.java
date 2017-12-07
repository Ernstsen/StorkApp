package stork.dk.storkapp.friendsSpinner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import stork.dk.storkapp.R;
import stork.dk.storkapp.communicationObjects.PublicUserObject;

/**
 * @author morten
 */
public class FriendsAdapter extends ArrayAdapter<PublicUserObject> {

    private List<PublicUserObject> userObjects;
    private List<PublicUserObject> checkedObjects = new ArrayList<>();
    Context mContext;

    // Search functionality
    private ArrayList<PublicUserObject> usersFilteredList;

    // View lookup cache
    private static class ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        CheckBox checkBox;
    }

    public FriendsAdapter(Context context, List<PublicUserObject> userObjects) {
        super(context, R.layout.listview_item_friends, userObjects);
        this.userObjects = userObjects;
        this.mContext=context;

        // Search functionality
        this.usersFilteredList = new ArrayList<PublicUserObject>();
        this.usersFilteredList.addAll(userObjects);
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final PublicUserObject userObject = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item_friends, parent, false);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name);
            viewHolder.emailTextView = (TextView) convertView.findViewById(R.id.email);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);

                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        if (checkedObjects.contains(userObject)) {
                            checkedObjects.remove(userObject);
                        }
                    } else {
                        checkBox.setChecked(true);
                        checkedObjects.add(userObject);
                    }
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.nameTextView.setText(userObject.getName());
        viewHolder.emailTextView.setText(userObject.getMail());

        // Return the completed view to render on screen
        return convertView;
    }

    public List<PublicUserObject> getCheckedObjects() {
        return checkedObjects;
    }

    // Search functionality
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        userObjects.clear();
        if (charText.length() == 0) {
            userObjects.addAll(usersFilteredList);
        } else {
            for (PublicUserObject user : usersFilteredList) {
                if (user.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    userObjects.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
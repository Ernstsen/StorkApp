package stork.dk.storkapp.friendsSpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import stork.dk.storkapp.R;
import stork.dk.storkapp.communicationObjects.PublicUserObject;

/**
 * @author morten
 */
public class FriendsAdapter extends ArrayAdapter<PublicUserObject> {

    private List<PublicUserObject> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        CheckBox checkBox;
    }

    public FriendsAdapter(Context context, List<PublicUserObject> data) {
        super(context, R.layout.listview_item_friends, data);
        this.dataSet = data;
        this.mContext=context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PublicUserObject dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item_friends, parent, false);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name);
            viewHolder.emailTextView = (TextView) convertView.findViewById(R.id.email);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.nameTextView.setText(dataModel.getName());
        viewHolder.emailTextView.setText(dataModel.getMail());
        // Return the completed view to render on screen
        return convertView;
    }
}
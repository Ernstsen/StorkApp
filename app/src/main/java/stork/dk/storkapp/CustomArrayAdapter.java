package stork.dk.storkapp;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mathiasjensen on 05/12/17.
 */

public class CustomArrayAdapter extends BaseAdapter implements ListAdapter {
    private final FriendsFragment fragment;
    private List<String> list = new ArrayList<>();
    private Context context;
    private int checked;


    public CustomArrayAdapter(ArrayList<String> list, Context context, FriendsFragment fragment) {
        this.fragment = fragment;
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_friend_list, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.delete_check);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO: ...
                if (isChecked) {
                    checked ++;
                } else {
                    checked --;
                }
                if(checked > 0){
                    fragment.showDeleteButton();
                }
                else{
                    fragment.hideDeleteButton();
                }
            }
        });

        return view;
    }
}

package stork.dk.storkapp.friendsSpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stork.dk.storkapp.R;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchableAdapter extends BaseAdapter implements Filterable {

    private List<PublicUserObjectWithCheckbox> originalData = null;
    private List<PublicUserObjectWithCheckbox> filteredData = null;
    private List<PublicUserObjectWithCheckbox> checkedObjects = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();


    public SearchableAdapter(Context context, List<PublicUserObjectWithCheckbox> data) {
        this.filteredData = this.originalData = data;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public PublicUserObjectWithCheckbox getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        final PublicUserObjectWithCheckbox userObject = getItem(position);
        int i = originalData.indexOf(userObject);
        final PublicUserObjectWithCheckbox originalUserObject = originalData.get(i);

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_friends, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.name);
            holder.emailTextView = (TextView) convertView.findViewById(R.id.email);
            holder.checkboxView = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);

                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.nameTextView.setText(userObject.getName());
        holder.emailTextView.setText(userObject.getMail());
        holder.checkboxView.setOnCheckedChangeListener(null);
        holder.checkboxView.setChecked(originalUserObject.isChecked());
        holder.checkboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                originalUserObject.setChecked(isChecked);

                if (isChecked) {
                    originalUserObject.setChecked(true);
                    if (!checkedObjects.contains(originalUserObject)) {
                        checkedObjects.add(originalUserObject);
                    }
                } else {
                    originalUserObject.setChecked(false);
                    if (checkedObjects.contains(originalUserObject)) {
                        checkedObjects.remove(originalUserObject);
                    }
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        CheckBox checkboxView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    public List<PublicUserObjectWithCheckbox> getCheckedObjects() {
        return checkedObjects;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            List<PublicUserObjectWithCheckbox> newList = new ArrayList<>();
            String filterableString;

            for (PublicUserObjectWithCheckbox userObject : originalData) {
                filterableString = userObject.getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    newList.add(userObject);
                }
            }

            results.values = newList;
            results.count = newList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<PublicUserObjectWithCheckbox>) results.values;
            notifyDataSetChanged();
        }
    }
}

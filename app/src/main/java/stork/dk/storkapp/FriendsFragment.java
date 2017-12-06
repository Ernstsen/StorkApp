package stork.dk.storkapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;

/**
 * @author Mathias, Johannes.
 */

public class FriendsFragment extends Fragment {

    private View rootView;
    private FloatingActionButton removeFriends;
    private FloatingActionButton addFriend;
    private ListView listView;
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = rootView.findViewById(R.id.friendList);

        Bundle args = getArguments();
        Integer userId = args.getInt(Constants.CURRENT_USER_KEY);
        String sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getCheckedItemCount() > 0) {
                    showDeleteButton();
                } else {
                    hideDeleteButton();
                }
            }
        });

        removeFriends = rootView.findViewById(R.id.removeFriends);
        removeFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                int itemCount = listView.getCount();

                for (int i = itemCount - 1; i >= 0; i--) {
                    if (checkedItemPositions.get(i)) {
                        //TODO: REMOVE ITEMS
                        hideDeleteButton();
                    }
                }
                checkedItemPositions.clear();
                adapter.notifyDataSetChanged();

            }
        });


        addFriend = rootView.findViewById(R.id.addFriendButton);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFriend = new Intent(getActivity(), AddFriend.class);
                addFriend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(addFriend);
            }
        });

        CommunicationsHandler.getFriends(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                UsersResponse resp = new Gson().fromJson(new String(responseBody), UsersResponse.class);

                populateWithObjects(resp.getUsers());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 403){
                    CommunicationErrorHandling.handle403(getActivity());
                } else if (statusCode == 404) {
                    Toast.makeText(getActivity(), "You don't seem to exist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "RETURNED " + Integer.toString(statusCode), Toast.LENGTH_LONG).show();
                }

            }
        });

        return rootView;
    }

    private void populate(List<String> items) {
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void populateWithObjects(List<PublicUserObject> users) {
        ArrayList<String> strings = new ArrayList<>();
        for (PublicUserObject user : users) {
            strings.add(user.getName());
        }
        populate(strings);
    }

    public void showDeleteButton() {
        removeFriends.setVisibility(View.VISIBLE);
        addFriend.setVisibility(View.INVISIBLE);
    }

    public void hideDeleteButton() {
        removeFriends.setVisibility(View.INVISIBLE);
        addFriend.setVisibility(View.VISIBLE);
    }
}

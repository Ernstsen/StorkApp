package stork.dk.storkapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
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

    private FloatingActionButton addFriend;
    private FloatingActionButton removeFriends;
    private FloatingActionButton createGroup;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private int checkedCount = 0;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = rootView.findViewById(R.id.friendList);

        Bundle args = getArguments();
        Integer userId = args.getInt(Constants.CURRENT_USER_KEY);
        String sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        searchFieldInit();

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        setShowAndHideListener();

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
                //Todo: maybe remove the below if it works w/o
                checkedCount = listView.getCheckedItemCount();
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
                populateWithUsers(resp.getUsers());
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

        createGroup = rootView.findViewById(R.id.createGroup);

        return rootView;
    }

    private void populate(List<String> items) {
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void populateWithUsers(List<PublicUserObject> users) {
        ArrayList<String> strings = new ArrayList<>();
        for (PublicUserObject user : users) {
            strings.add(user.getName());
        }
        Collections.sort(strings);
        populate(strings);
    }

    public void setShowAndHideListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getCheckedItemCount() > checkedCount) {
                    showDeleteButton();
                } else {
                    hideDeleteButton();
                }
            }
        });
    }

    public void showDeleteButton() {
        removeFriends.setVisibility(View.VISIBLE);
        addFriend.setVisibility(View.INVISIBLE);
        createGroup.setVisibility(View.VISIBLE);
    }

    public void hideDeleteButton() {
        removeFriends.setVisibility(View.INVISIBLE);
        addFriend.setVisibility(View.VISIBLE);
        createGroup.setVisibility(View.INVISIBLE);
    }

    public void searchFieldInit(){
        EditText searchField = (EditText) rootView.findViewById(R.id.searchField);
        listView.setTextFilterEnabled(true);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence ag0, int ag1, int ag2, int ag3) {
            }

            @Override
            public void onTextChanged(CharSequence ag0, int ag1, int ag2, int ag3) {
                adapter.getFilter().filter(ag0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            adapter.notifyDataSetChanged();
        }
    }
}

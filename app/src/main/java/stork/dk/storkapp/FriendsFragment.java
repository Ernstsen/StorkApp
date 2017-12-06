package stork.dk.storkapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import stork.dk.storkapp.communicationObjects.FriendChangeRequest;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;

/**
 * @author Mathias, Johannes.
 */

public class FriendsFragment extends Fragment {
    private SettingsFragment thisInstance;
    private FloatingActionButton addFriend;
    private FloatingActionButton removeFriends;
    private FloatingActionButton createGroup;
    private ListView listView;
    private ArrayAdapter<PublicUserObject> adapter;
    private int checkedCount = 0;
    private View rootView;
    private Integer userId;
    private String sessionId;
    private List<Integer> friendsToRemove;
    private ArrayList<PublicUserObject> items;
    private FriendChangeRequest req;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = rootView.findViewById(R.id.friendList);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        req = new FriendChangeRequest();
        friendsToRemove = new ArrayList<Integer>();

        removeFriends = rootView.findViewById(R.id.removeFriends);
        addFriend = rootView.findViewById(R.id.addFriendButton);
        createGroup = rootView.findViewById(R.id.createGroup);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        req.setSessionId(sharedPref.getString(Constants.CURRENT_SESSION_KEY, ""));
        req.setId(sharedPref.getInt(Constants.CURRENT_USER_KEY, 0));
        req.setAction(FriendChangeRequest.ActionEnum.REMOVE);


        searchFieldInit();

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        setShowAndHideListener();

        removeFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItemPos = listView.getCheckedItemPositions();

                for (int i = 0; i < listView.getCount(); i++) {
                    if (checkedItemPos.get(i)) {
                        getIdFromEmail(items.get(i));
                        items.remove(items.get(i));
                    }
                }
                removeFriends();
                checkedItemPos.clear();
                adapter.notifyDataSetChanged();
                friendsToRemove.clear();

                hideDeleteButton();
                //Todo: maybe remove the below if it works w/o
                checkedCount = listView.getCheckedItemCount();
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFriend = new Intent(getActivity(), AddFriendsActivity.class);
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
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getActivity());
                } else if (statusCode == 404) {
                    Toast.makeText(getActivity(), "You don't seem to exist", Toast.LENGTH_LONG).show();
                } else {
                    //For Debug Purposes:
                    //Toast.makeText(getActivity(), "RETURNED " + Integer.toString(statusCode), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void populate(List<PublicUserObject> items) {
        if (getActivity() != null) {
            adapter = new ArrayAdapter<PublicUserObject>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }

    private void populateWithUsers(List<PublicUserObject> users) {
        items = new ArrayList<>();
        items.addAll(users);
        Collections.sort(items);
        populate(items);
    }

    public void setShowAndHideListener() {
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

    public void searchFieldInit() {
        if (getActivity() != null) {
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onResume();
        }
    }

    private void getIdFromEmail(PublicUserObject user) {
        friendsToRemove.add(user.getUserId());
    }

    public void removeFriends() {
        req.setFriends(friendsToRemove);

        CommunicationsHandler.changeFriends(getActivity(), req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!friendsToRemove.isEmpty()) {
                    Toast.makeText(getActivity(), "Friends Removed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(thisInstance);
                } else if (statusCode == 404 || statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package stork.dk.storkapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
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
import stork.dk.storkapp.communicationObjects.FriendChangeRequest;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;
import stork.dk.storkapp.communicationObjects.helperObjects.UserObject;

/**
 * @author Johannes
 */
public class AddFriend extends AppCompatActivity {
    private SettingsFragment thisInstance;
    private ListView usersList;
    private ArrayAdapter<PublicUserObject> adapter;
    private ArrayList<PublicUserObject> items;
    private FriendChangeRequest req;
    private int user;
    private List<Integer> friendsToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        friendsToAdd = new ArrayList<>();

        req = new FriendChangeRequest();
        req.setSessionId(sharedPref.getString(Constants.CURRENT_SESSION_KEY, ""));
        req.setId(sharedPref.getInt(Constants.CURRENT_USER_KEY, 0));
        req.setAction(FriendChangeRequest.ActionEnum.ADD);

        CommunicationsHandler.getUsers(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                UsersResponse usersResponse = new Gson().fromJson(new String(responseBody), UsersResponse.class);
                populate(usersResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getActivity());
                }

            }
        });

        FloatingActionButton fab = findViewById(R.id.finish_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checkedItemPos = usersList.getCheckedItemPositions();

                for (int i = 0; i < usersList.getCount(); i++) {
                    if (checkedItemPos.get(i)) {
                        getIdFromEmail(items.get(i));
                    }
                }
                addFriends();
                checkedItemPos.clear();
                adapter.notifyDataSetChanged();
                friendsToAdd.clear();
                getActivity().finish();
            }
        });
    }

    private void populate(UsersResponse usersResponse) {
        items = new ArrayList<>();
        items.addAll(usersResponse.getUsers());
        usersList = findViewById(R.id.users_list);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
        usersList.setAdapter(adapter);
        usersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private void getIdFromEmail(PublicUserObject user) {
        friendsToAdd.add(user.getUserId());
    }

    public void addFriends() {
        req.setFriends(friendsToAdd);

        CommunicationsHandler.changeFriends(this, req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!friendsToAdd.isEmpty()) {
                    Toast.makeText(getActivity(), "Friends added!", Toast.LENGTH_SHORT).show();
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

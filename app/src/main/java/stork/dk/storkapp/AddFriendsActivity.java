package stork.dk.storkapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.FriendChangeRequest;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;
import stork.dk.storkapp.Adapter.PublicUserObjectWithCheckbox;
import stork.dk.storkapp.Adapter.SearchableAdapterAddFriends;

/**
 * @author Johannes, Mathias, Morten
 */
public class AddFriendsActivity extends AppCompatActivity {
    private ListView usersList;
    private FloatingActionButton fab;

    private SearchableAdapterAddFriends adapter;
    private ArrayList<PublicUserObject> items;
    private FriendChangeRequest req;
    private int userId;
    private String friendsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usersList = findViewById(R.id.users_list);
        fab = findViewById(R.id.finish_fab);

        SharedPreferences sharedPref = getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedPref.getInt(Constants.CURRENT_USER_KEY, 0);
        friendsJson = sharedPref.getString(Constants.FRIENDS_LIST, "");

        req = new FriendChangeRequest();
        req.setSessionId(sharedPref.getString(Constants.CURRENT_SESSION_KEY, ""));
        req.setId(userId);
        req.setAction(FriendChangeRequest.ActionEnum.ADD);

        CommunicationsHandler.getUsers(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                UsersResponse usersResponse = new Gson().fromJson(new String(responseBody), UsersResponse.class);
                populateListView(usersResponse);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<PublicUserObjectWithCheckbox> checkedObjects = adapter.getCheckedObjects();

                        List<PublicUserObject> friendsToAdd = new ArrayList<>();
                        for (PublicUserObjectWithCheckbox userObjectWithCheckbox : checkedObjects) {
                            PublicUserObject userObject
                                    = new PublicUserObject(userObjectWithCheckbox.getUserId(), userObjectWithCheckbox.getName(), userObjectWithCheckbox.getMail());
                            friendsToAdd.add(userObject);
                        }
                        addFriends(friendsToAdd);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getActivity());
                }

            }
        });
    }

    private void populateListView(UsersResponse usersResponse) {
        items = new ArrayList<>(filerUsers(usersResponse.getUsers()));
        Collections.sort(items);

        List<PublicUserObjectWithCheckbox> userObjectsWithCheckbox = new ArrayList<>();
        for (PublicUserObject userObject : items) {
            PublicUserObjectWithCheckbox userObjectWithCheckbox
                    = new PublicUserObjectWithCheckbox(userObject.getUserId(), userObject.getName(), userObject.getMail(), false);
            userObjectsWithCheckbox.add(userObjectWithCheckbox);
        }

        adapter = new SearchableAdapterAddFriends(getActivity(), userObjectsWithCheckbox);
        usersList.setAdapter(adapter);
        searchFieldInit();
    }

    private List<PublicUserObject> deserializeFriendsJson(String friendsJson) {
        return new Gson().fromJson(friendsJson, new TypeToken<List<PublicUserObject>>(){}.getType());
    }

    private List<PublicUserObject> filerUsers(List<PublicUserObject> userObjects) {
        List<PublicUserObject> filteredUserObjects = new ArrayList<>();
        List<PublicUserObject> currentFriends = deserializeFriendsJson(friendsJson);

        for (PublicUserObject userObject : userObjects) {
            boolean isNotUser = userObject.getUserId() != userId;
            boolean isNotCurrentFriend = !currentFriends.contains(userObject);
            if (isNotUser && isNotCurrentFriend) {
                filteredUserObjects.add(userObject);
            }
        }

        return filteredUserObjects;
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private List<Integer> getIdsFromUserObjects(List<PublicUserObject> userObjects) {
        List<Integer> ids = new ArrayList<>();

        for (PublicUserObject userObject : userObjects) {
            ids.add(userObject.getUserId());
        }

        return ids;
    }

    private void addFriends(final List<PublicUserObject> friendsToAdd) {
        final List<Integer> idsOfFriendsToAdd = getIdsFromUserObjects(friendsToAdd);
        req.setFriends(idsOfFriendsToAdd);

        CommunicationsHandler.changeFriends(this, req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(!idsOfFriendsToAdd.isEmpty()) {
                    items.removeAll(friendsToAdd);
                    getActivity().finish();
                    Toast.makeText(getActivity(), "Friends added!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getActivity());
                } else if (statusCode == 404 || statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void searchFieldInit() {
            final EditText searchField = (EditText) findViewById(R.id.searchFieldAddFriend);
            if(usersList != null) {
                usersList.setTextFilterEnabled(true);
            }
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

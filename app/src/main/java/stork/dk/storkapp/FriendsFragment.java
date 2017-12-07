package stork.dk.storkapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
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
import stork.dk.storkapp.communicationObjects.ChangeGroupRequest;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.FriendChangeRequest;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;
import stork.dk.storkapp.communicationObjects.helperObjects.UserObject;

/**
 * @author Mathias, Johannes.
 */

public class FriendsFragment extends Fragment {
    private FloatingActionButton addFriend;
    private FloatingActionButton removeFriends;
    private FloatingActionButton createGroup;
    private ListView listView;
    private ArrayAdapter<PublicUserObject> adapter;
    private int checkedCount = 0;
    private View rootView;
    private Integer userId;
    private String sessionId;
    private List<Integer> selectedItems;
    private ArrayList<PublicUserObject> items;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = rootView.findViewById(R.id.friendList);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        selectedItems = new ArrayList<Integer>();

        removeFriends = rootView.findViewById(R.id.removeFriends);
        addFriend = rootView.findViewById(R.id.addFriendButton);
        createGroup = rootView.findViewById(R.id.createGroup);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        searchFieldInit();

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        setShowAndHideListener();

        removeFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItemPos = listView.getCheckedItemPositions();

                ArrayList<PublicUserObject> tempSelected = new ArrayList<>();
                for (int i = 0; i < listView.getCount(); i++) {
                    if (checkedItemPos.get(i)) {
                        tempSelected.add(items.get(i));
                        selectedItems.add(items.get(i).getUserId());
                    }
                }
                items.removeAll(tempSelected);
                removeFriends();
                checkedItemPos.clear();
                adapter.notifyDataSetChanged();
                selectedItems.clear();

                checkedCount = listView.getCheckedItemCount();
                hideDeleteButton();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItemPos = listView.getCheckedItemPositions();
                ArrayList<Integer> selectedItems = new ArrayList<>();
                for (int i = 0; i < listView.getCount(); i++) {
                    if (checkedItemPos.get(i)) {
                        selectedItems.add(items.get(i).getUserId());
                    }
                }
                createGroup(selectedItems);
                checkedItemPos.clear();
                adapter.notifyDataSetChanged();

                checkedCount = listView.getCheckedItemCount();
                hideDeleteButton();
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendsToSharedPreference(items);
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

    private void addFriendsToSharedPreference(List<PublicUserObject> friends) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String friendsJson = new Gson().toJson(friends);
        editor.putString(Constants.FRIENDS_LIST, friendsJson);
        editor.apply();
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


    public void createGroup(final List<Integer> selectedItems) {

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        new AlertDialog.Builder(getActivity())
                .setTitle("Name the group")
                .setView(input)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setPositiveButton("Create Group", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ChangeGroupRequest req = new ChangeGroupRequest();
                        SharedPreferences sharedPref = getFriendsFragmentActivity().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);

                        req.setSessionId(sharedPref.getString(Constants.CURRENT_SESSION_KEY, null));
                        req.setUserId(sharedPref.getInt(Constants.CURRENT_USER_KEY, 0));
                        req.setAdd(selectedItems);
                        req.setName(input.getText().toString());

                        CommunicationsHandler.changeGroup(getFriendsFragmentActivity(), req, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 201) {
                                    Toast.makeText(getFriendsFragmentActivity(), "Group created!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getFriendsFragmentActivity(), "Code: " + statusCode, Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                if (statusCode == 403) {
                                    CommunicationErrorHandling.handle403(getFriendsFragmentActivity());
                                } else {
                                    Toast.makeText(getFriendsFragmentActivity(), "Code: " + statusCode, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .show();

        //TODO: Update Group ListView
    }

    public void removeFriends() {
        FriendChangeRequest req = new FriendChangeRequest();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        req.setSessionId(sharedPref.getString(Constants.CURRENT_SESSION_KEY, ""));
        req.setId(sharedPref.getInt(Constants.CURRENT_USER_KEY, 0));
        req.setAction(FriendChangeRequest.ActionEnum.REMOVE);
        req.setFriends(selectedItems);

        CommunicationsHandler.changeFriends(getActivity(), req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (!selectedItems.isEmpty()) {
                    Toast.makeText(getActivity(), "Friends Removed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getFriendsFragmentActivity());
                } else if (statusCode == 404 || statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private FragmentActivity getFriendsFragmentActivity() {
        return getActivity();
    }
}

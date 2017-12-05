package stork.dk.storkapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.*;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;
import stork.dk.storkapp.communicationObjects.helperObjects.UserObject;
import stork.dk.storkapp.friendsSpinner.FriendsAdapter;
import stork.dk.storkapp.friendsSpinner.Group;

/**
 * @author Mathias, Johannes.
 */

public class FriendsFragment extends Fragment {
    private View rootView;
    private Integer userId;
    private String sessionId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        FloatingActionButton addFriend = rootView.findViewById(R.id.addFriendButton);
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

                populate(resp.getUsers());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    Intent login = new Intent(getActivity(), AddFriend.class);
                    startActivity(login);
                    getActivity().finish();
                    Toast.makeText(getActivity(), "Error connecting to server.", Toast.LENGTH_LONG).show();
                } else if (statusCode == 404) {
                    Toast.makeText(getActivity(), "You don't seem to exist", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "RETURNED " + Integer.toString(statusCode), Toast.LENGTH_LONG).show();
                }

            }
        });


        //TEST for swipe menu
        SwipeMenuListView listView = (SwipeMenuListView) getActivity().findViewById(R.id.listView);
        ArrayList<String> list = new ArrayList<>();
        list.add("test");
        list.add("test2");
        list.add("test3");

       // ArrayAdapter adapter = new ArrayAdapter();


        return rootView;
    }

    private void populate(List<PublicUserObject> users) {
        ListView friendList = rootView.findViewById(R.id.friendList);
        final FriendsAdapter adapter = new FriendsAdapter(getActivity().getApplicationContext(), users);
        friendList.setAdapter(adapter);
    }
}

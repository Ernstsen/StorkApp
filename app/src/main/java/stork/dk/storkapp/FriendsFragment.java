package stork.dk.storkapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

        FloatingActionButton addFriend = (FloatingActionButton) rootView.findViewById(R.id.addFriendButton);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "TEEST", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), "FUCK", Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    private void populate(List<String> items) {
        ListView viewById = rootView.findViewById(R.id.friendList);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        viewById.setAdapter(adapter);
    }

    private void populateWithObjects(List<PublicUserObject> users) {
        ArrayList<String> strings = new ArrayList<>();
        for (PublicUserObject user : users) {
            strings.add(user.getName());
        }
        populate(strings);
    }
}

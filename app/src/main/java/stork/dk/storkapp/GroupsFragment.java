package stork.dk.storkapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import stork.dk.storkapp.communicationObjects.GroupsResponse;
import stork.dk.storkapp.friendsSpinner.Group;


/**
 * @author morten
 */
public class GroupsFragment extends Fragment {
    private View rootView;
    private Integer userId;
    private String sessionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        CommunicationsHandler.getGroups(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GroupsResponse resp = new Gson().fromJson(new String(responseBody), GroupsResponse.class);

                populateListView(resp.getGroups());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 403){
                    CommunicationErrorHandling.handle403(getActivity());
                }
            }
        });

        return rootView;
    }

    private void populateListView(List<Group> groups) {
        ListView groupList = rootView.findViewById(R.id.groupList);

        ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(getActivity(), android.R.layout.simple_list_item_1, groups);
        groupList.setAdapter(adapter);

        groupList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group selectedItem = (Group) parent.getItemAtPosition(position);
            }
        });
    }
}
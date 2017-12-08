package stork.dk.storkapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import stork.dk.storkapp.utils.ExpandableListAdapter;

import android.widget.ExpandableListView;

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
import stork.dk.storkapp.communicationObjects.GroupsResponse;
import stork.dk.storkapp.adapter.Friend;
import stork.dk.storkapp.adapter.Group;


/**
 * @author Mathias, Morten
 */
public class GroupsFragment extends Fragment {
    private View rootView;
    private HashMap<String, List<String>> listDataChild;
    private HashMap<Integer, Integer> groupsIdAndPos;
    private ArrayList<String> listDataHeader;
    private ExpandableListAdapter adapter;
    private String sessionId;
    private int userId;
    private ExpandableListView groupList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        groupsIdAndPos = new HashMap<>();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        groupList = rootView.findViewById(R.id.groupList);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        populateList();
    }

    public void populateList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        CommunicationsHandler.getGroups(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GroupsResponse resp = new Gson().fromJson(new String(responseBody), GroupsResponse.class);
                int i = 0;
                Collections.sort(resp.getGroups());
                if(listDataHeader.size() != resp.getGroups().size()) {
                    for (Group group : resp.getGroups()) {
                        listDataHeader.add(group.getName());
                        groupsIdAndPos.put(listDataHeader.indexOf(group.getName()), group.getId());
                        if (group.getOwner() == userId) {
                            //if owner, do something
                        }
                        List<String> grp = new ArrayList<>();
                        for (Friend friend : group.getFriends()) {
                            grp.add(friend.getName());
                        }
                        listDataChild.put(listDataHeader.get(i), grp);

                        i++;
                    }
                }
                setAdapter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getActivity());
                }
            }
        });
        adapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, groupsIdAndPos);
    }

    public void setAdapter() {
        adapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild, groupsIdAndPos);
        groupList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            populateList();
            adapter.notifyDataSetChanged();
        }
    }
}
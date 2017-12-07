package stork.dk.storkapp;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.GroupsResponse;
import stork.dk.storkapp.friendsSpinner.Friend;
import stork.dk.storkapp.friendsSpinner.Group;


/**
 * @author morten
 */
public class GroupsFragment extends Fragment {
    private View rootView;
    private Integer userId;
    private String sessionId;

    //For testing:
    private HashMap<String, List<String>> listDataChild = new HashMap<>();
    private ArrayList<String> listDataHeader = new ArrayList<>();

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
                int i = 0;
                List<String> grp;
                for (Group group : resp.getGroups()) {
                    listDataHeader.add(group.getName());

                    grp = new ArrayList<>();
                    for (Friend friend : group.getFriends()) {
                        grp.add(friend.getName());
                    }
                    listDataChild.put(listDataHeader.get(i),grp);
                    grp.clear();
                    i ++;
                }
                System.out.println(listDataHeader);
                System.out.println(listDataChild);
                //populateListView(resp.getGroups());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(getActivity());
                }
            }
        });

        //TODO: Remove test:
        populateExpList();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void testPopulate(){
        //TODO: remove test:
        listDataHeader.add("Group1");
        listDataHeader.add("Group2");
        listDataHeader.add("Group3");


        List<String> test1 = new ArrayList<>();
        test1.add("111");
        test1.add("111");
        test1.add("111");
        test1.add("111");

        List<String> test2 = new ArrayList<>();
        test2.add("222");
        test2.add("222");
        test2.add("222");
        test2.add("222");

        List<String> test3 = new ArrayList<>();
        test3.add("333");
        test3.add("333");
        test3.add("333");
        test3.add("333");

        listDataChild.put(listDataHeader.get(0),test1);
        listDataChild.put(listDataHeader.get(1), test2);
        listDataChild.put(listDataHeader.get(2), test3);
    }

    public void populateExpList(){
        ExpandableListView groupList = rootView.findViewById(R.id.groupList);
        //Collections.sort(groups);

        stork.dk.storkapp.utils.ExpandableListAdapter adapter = new stork.dk.storkapp.utils.ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        groupList.setAdapter(adapter);
    }


    private void populateListView(List<Group> groups) {
        ExpandableListView groupList = rootView.findViewById(R.id.groupList);
        Collections.sort(groups);

        //ArrayAdapter<Group> adapter = new ArrayAdapter<Group>(getActivity(), android.R.layout.simple_list_item_1, groups);
        stork.dk.storkapp.utils.ExpandableListAdapter adapter1 = new stork.dk.storkapp.utils.ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        groupList.setAdapter(adapter1);

//        groupList.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Group selectedItem = (Group) parent.getItemAtPosition(position);
//                Intent editGroup = new Intent(getActivity(), EditGroupActivity.class);
//                editGroup.putExtra("group", selectedItem);
//                startActivity(editGroup);
//            }
//        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onResume();
        }
    }
}
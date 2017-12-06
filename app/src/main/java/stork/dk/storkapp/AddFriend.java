package stork.dk.storkapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.PublicUserObject;
import stork.dk.storkapp.communicationObjects.UsersResponse;

/**
 * @author Johannes
 */
public class AddFriend extends AppCompatActivity {
    private ListView usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.finish_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        CommunicationsHandler.getUsers(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                UsersResponse usersResponse = new Gson().fromJson(new String(responseBody), UsersResponse.class);
                populate(usersResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    Intent login = new Intent(getActivity(), AddFriend.class);
                    startActivity(login);
                    finish();
                    Toast.makeText(getActivity(), "Error connecting to server.", Toast.LENGTH_LONG).show();
                }

            }
        });
        // BRUG NEDENSTÅENDE TIL AT FÅ FLERE VALGTE OBJEKTER
        //SparseBooleanArray checkedItemPositions = usersList.getCheckedItemPositions();
        //int itemCount = listView.getCount();

        //for (int i = itemCount - 1; i >= 0; i--) {
        //    if (checkedItemPositions.get(i)) {
                    //Remove items here
        //    }
        //}
        //checkedItemPositions.clear();
        //adapter.notifyDataSetChanged();

    }

    private void populate(UsersResponse usersResponse) {
        ArrayList<String> items = new ArrayList<>();
        for (PublicUserObject publicUserObject : usersResponse.getUsers()) {
            items.add(publicUserObject.getMail());
        }

        usersList = findViewById(R.id.users_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
        usersList.setAdapter(adapter);
        usersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

}

package stork.dk.storkapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.helperObjects.UserObject;


/**
 * @author Mathias
 */
public class Settings extends AppCompatActivity {
    private final static Gson gson = new Gson();
    private String username;
    private EditText nameField;
    private String newUserName;
    private SharedPreferences sharedPref;
    private Settings thisInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUserName();

        buttonFunction();

        nameField = (EditText) findViewById(R.id.username);


    }

    public void readChanges() {
        //TODO: DO something here to change name
        if (nameField != null) {
            newUserName = nameField.getText().toString();
        }
    }

    private void buttonFunction() {
        Button saveChanges = (Button) findViewById(R.id.saveChanges);
        readChanges();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Upload username
            }
        });

    }

    public void getUserName() {
        sharedPref = getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("sessionId", sharedPref.getString(Constants.CURRENT_SESSION_KEY, ""));
        params.put("userId", String.valueOf(sharedPref.getInt(Constants.CURRENT_USER_KEY, 0)));

        CommunicationsHandler.getUser(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                UserObject user = new Gson().fromJson(new String(responseBody), UserObject.class);
                username = user.getName();
                nameField.setText(username);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(thisInstance);
                } else if (statusCode == 404 || statusCode == 500) {
                    Toast.makeText(Settings.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

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
import stork.dk.storkapp.communicationObjects.ChangeUserRequest;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.helperObjects.UserObject;


/**
 * @author Mathias
 */
public class SettingsFragment extends AppCompatActivity {
    private String username;
    private EditText nameField;
    private SharedPreferences sharedPref;
    private EditText oldPwField;
    private EditText newPwField;
    private EditText matchPwField;
    private ChangeUserRequest req;
    private SettingsFragment thisInstance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);

        getUserName();

        buttonFunction();

        nameField = (EditText) findViewById(R.id.username);
        oldPwField = (EditText) findViewById(R.id.currentPassword);
        newPwField = (EditText) findViewById(R.id.newPassword);
        matchPwField = (EditText) findViewById(R.id.repeatPassword);
    }

    private void buttonFunction() {
        final Button saveChanges = (Button) findViewById(R.id.saveChanges);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPwField.getText().toString().equals("")){
                    Toast.makeText(SettingsFragment.this, "Need password to save changes.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!newPwField.getText().toString().equals("") && !newPwField.getText().toString().equals(matchPwField.getText().toString())) {
                    Toast.makeText(SettingsFragment.this, "New Password doesn't match", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!newPwField.getText().toString().equals("") && matchPwField.getText().toString().equals("")) {
                    Toast.makeText(SettingsFragment.this, "New Password must be supplied twice", Toast.LENGTH_LONG).show();
                } else {
                    doChanges();
                }
            }
        });

    }

    public void doChanges() {
        req = new ChangeUserRequest();
        if (!nameField.getText().toString().equals("") && !nameField.getText().toString().equals(username)) {
            req.setName(nameField.getText().toString());
        }
        if (!oldPwField.getText().toString().equals("")) {
            req.setPassword(oldPwField.getText().toString());
        }
        if (!oldPwField.getText().toString().equals("") && newPwField.getText().toString().equals(matchPwField.getText().toString())) {
            req.setNewPassword(matchPwField.getText().toString());
        }
        req.setSessionId(sharedPref.getString(Constants.CURRENT_SESSION_KEY, ""));
        req.setId(sharedPref.getInt(Constants.CURRENT_USER_KEY, 0));


        CommunicationsHandler.editUser(this, req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ChangeUserRequest user = new Gson().fromJson(new String(responseBody), ChangeUserRequest.class);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Constants.CURRENT_SESSION_KEY, user.getSessionId());
                editor.putString(Constants.REMEMBER_ME_PASSWORD, user.getPassword());
                editor.apply();
                Toast.makeText(SettingsFragment.this, "Changes Saved.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 403) {
                    CommunicationErrorHandling.handle403(thisInstance);
                } else if (statusCode == 404) {
                    Toast.makeText(SettingsFragment.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 500) {
                    Toast.makeText(SettingsFragment.this, "Error Connecting to server.", Toast.LENGTH_LONG).show();
                }
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
                } else if (statusCode == 404) {
                    Toast.makeText(SettingsFragment.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                } else if (statusCode == 500) {
                    Toast.makeText(SettingsFragment.this, "Error Connecting to server.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

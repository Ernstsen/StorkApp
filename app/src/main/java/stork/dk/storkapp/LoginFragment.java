package stork.dk.storkapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.LoginRequest;

/**
 * @author Mathias, Johannes
 */

public class LoginFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private boolean clicked;
    private SharedPreferences.Editor editor;
    private EditText mailField;
    private EditText passwordField;

    public LoginFragment() {
    }

    public static LoginFragment login(int sectionNumber) {
        LoginFragment fragment = new LoginFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        CheckBox rememberMe = (CheckBox) rootView.findViewById(R.id.remember_me_checkbox);

        clicked = false;
        editor = sharedPref.edit();
        mailField = rootView.findViewById(R.id.email);
        passwordField = rootView.findViewById(R.id.password);


        Button loginButton = (Button) rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(rootView);
            }
        });


        if (sharedPref.getBoolean(Constants.REMEMBER_ME_CHECK, false)) {
            rememberMe.setChecked(true);
        }

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(Constants.REMEMBER_ME_CHECK, true);
                } else {
                    editor.putBoolean(Constants.REMEMBER_ME_CHECK, false);
                }
                editor.apply();
            }
        });

        if (rememberMe.isChecked()) {
            mailField.setText(sharedPref.getString(Constants.REMEMBER_ME_EMAIL, ""));
            passwordField.setText(sharedPref.getString(Constants.REMEMBER_ME_PASSWORD, ""));
        }

        return rootView;
    }

    public void login(View v) {
        if (!clicked) {
            clicked = true;

            LoginRequest req = new LoginRequest();
            mailField = (EditText) v.findViewById(R.id.email);
            if (mailField == null || mailField.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "No email supplied", Toast.LENGTH_LONG).show();
                return;
            }
            req.setMail(mailField.getText().toString());
            passwordField = v.findViewById(R.id.password);
            if (passwordField == null || passwordField.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "No password supplied", Toast.LENGTH_LONG).show();
                return;
            }
            req.setPassword(passwordField.getText().toString());

            CommunicationsHandler.login(getActivity(), req, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    LoginRequest resp = new Gson().fromJson(new String(responseBody), LoginRequest.class);
                    FragmentActivity activity = getActivity();

                    editor.putBoolean(Constants.LOGGED_IN_KEY, true);
                    editor.putInt(Constants.CURRENT_USER_KEY, resp.getUserId());
                    editor.putString(Constants.CURRENT_SESSION_KEY, resp.getSessionId());
                    editor.putString(Constants.REMEMBER_ME_EMAIL, mailField.getText().toString());
                    editor.putString(Constants.REMEMBER_ME_PASSWORD, passwordField.getText().toString());
                    editor.apply();

                    Toast.makeText(activity, "Logged In!", Toast.LENGTH_LONG).show();

                    Intent loginSuccess = new Intent(getActivity(), MainActivity.class);
                    loginSuccess.putExtra("fromLoginPage", "loggedIn");
                    startActivity(loginSuccess);
                    getActivity().finish();
                    clicked = false;

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 403) {
                        CommunicationErrorHandling.handle403(getActivity());
                    } else if (statusCode == 404) {
                        Toast.makeText(getActivity(), "No match for provided email and password", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getActivity(), "Error Connecting to server.", Toast.LENGTH_LONG).show();
                    }
                    clicked = false;
                }
            });
        }
    }
}

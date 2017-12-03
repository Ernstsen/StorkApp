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
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.LoginRequest;

/**
 * @author Mathias, Johannes
 */

public class LoginFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private boolean clicked;


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
        clicked = false;

        Button loginButton = (Button) rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(rootView);
            }
        });

        return rootView;
    }

    public void login(View v) {
        //TODO:Create real login
        if (!clicked) {
            clicked = true;
            LoginRequest req = new LoginRequest();
            EditText mailField = (EditText) v.findViewById(R.id.email);
            if (mailField == null || mailField.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "No email supplied", Toast.LENGTH_LONG).show();
                return;
            }
            req.setMail(mailField.getText().toString());
            EditText passwordField = v.findViewById(R.id.password);
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

                    SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.LOGGED_IN_KEY, true);
                    editor.putInt(Constants.CURRENT_USER_KEY, resp.getUserId());
                    Toast.makeText(activity, "Logged In!", Toast.LENGTH_LONG).show();
                    Intent loginSuccess = new Intent(getActivity(), MainActivity.class);
                    loginSuccess.putExtra("fromLoginPage", "loggedIn");
                    startActivity(loginSuccess);
                    getActivity().finish();
                    editor.apply();
                    clicked = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 404) {
                        Toast.makeText(getActivity(), "No match for provided email and password", Toast.LENGTH_LONG).show();
                    }
                    clicked = false;
                }
            });

//            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
//            boolean loggedIn = sharedPreferences.getBoolean(Constants.LOGGED_IN_KEY, false);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean(Constants.LOGGED_IN_KEY, true);
//            editor.putInt("currentUser", userId);
//            editor.apply();

            //TODO: Should only show toast if login is successful
//            Toast.makeText(getActivity(), "Logged in!",
//                    Toast.LENGTH_LONG).show();
//
//            Intent loginSuccess = new Intent(getActivity(), MainActivity.class);
//            loginSuccess.putExtra("fromLoginPage", "loggedIn");
//            startActivity(loginSuccess);
//            getActivity().finish();
        }
    }
}

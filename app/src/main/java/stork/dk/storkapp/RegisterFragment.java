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
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.RegisterUserRequest;

/**
 * @author Mathias, Johannes
 */
public class RegisterFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private boolean clicked;

    public static RegisterFragment register(int sectionNumber) {
        RegisterFragment fragment = new RegisterFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        Button loginButton = (Button) rootView.findViewById(R.id.register_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(rootView);
            }
        });

        return rootView;
    }

    private void register(View v) {
        if (!clicked) {
            clicked = true;
            RegisterUserRequest req = new RegisterUserRequest();

            EditText nameField = (EditText) v.findViewById(R.id.name);
            String name = nameField.getText().toString();
            if (name.equals("")) {
                Toast.makeText(getActivity(), "No name supplied", Toast.LENGTH_LONG).show();
                return;
            }
            req.setName(name);

            EditText mailField = (EditText) v.findViewById(R.id.email);
            String email = mailField.getText().toString();
            if (email.equals("")) {
                Toast.makeText(getActivity(), "No email supplied", Toast.LENGTH_LONG).show();
                return;
            }
            req.setMail(email);

            EditText passwordField = v.findViewById(R.id.password);
            String password = passwordField.getText().toString();
            if (password.equals("")) {
                Toast.makeText(getActivity(), "No password supplied", Toast.LENGTH_LONG).show();
                return;
            }
            EditText passwordField2 = v.findViewById(R.id.password2);
            String password2 = passwordField2.getText().toString();
            if (password2.equals("")) {
                Toast.makeText(getActivity(), "Password must be supplied twice", Toast.LENGTH_LONG).show();
                return;
            }
            if (!password2.equals(password)) {
                Toast.makeText(getActivity(), "Password MUST match", Toast.LENGTH_LONG).show();
            }
            req.setPassword(password);

            CommunicationsHandler.register(getActivity(), req, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    RegisterUserRequest resp = new Gson().fromJson(new String(responseBody), RegisterUserRequest.class);
                    FragmentActivity activity = getActivity();

                    SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.LOGGED_IN_KEY, true);
                    editor.putInt(Constants.CURRENT_USER_KEY, resp.getUserId());
                    editor.putString(Constants.CURRENT_SESSION_KEY, resp.getSessionId());
                    editor.apply();
                    Toast.makeText(activity, "Logged In!", Toast.LENGTH_LONG).show();

                    Intent loginSuccess = new Intent(getActivity(), MainActivity.class);
                    loginSuccess.putExtra("fromRegisterPage", "loggedIn");
                    startActivity(loginSuccess);
                    getActivity().finish();
                    clicked = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 404) {
                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                    clicked = false;
                }
            });
        }

    }
}

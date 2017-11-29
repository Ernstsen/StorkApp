package stork.dk.storkapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by mathiasjensen on 29/11/17.
 */

public class LoginFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private boolean clicked;

    private static final String APP_SHARED_PREFS = "login_preference";
    private SharedPreferences sharedPreferences;
    private int userId;

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
                login();
            }
        });

        return rootView;
    }

    public void login(){
        //TODO:Create real login
        if(!clicked) {
            clicked = true;
            System.out.println("Logged in");

            sharedPreferences = this.getActivity().getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
            boolean loggedIn = sharedPreferences.getBoolean("loggedInState", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("loggedInState", true);
            editor.putInt("currentUser", userId);
            editor.apply();

            //TODO: Should only show toast if login is successful
            Toast.makeText(getActivity(),"Logged in!",
                    Toast.LENGTH_LONG).show();

            Intent loginSuccess = new Intent(getActivity(), MainActivity.class);
            loginSuccess.putExtra("fromLoginPage", "loggedIn");
            startActivity(loginSuccess);
            getActivity().finish();
        }
    }
}

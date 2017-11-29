package stork.dk.storkapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author mathiasjensen
 */
public class Login extends AppCompatActivity {

    private static final String APP_SHARED_PREFS = "login_preference";
    private SharedPreferences sharedPreferences;
    private boolean loggedIn;
    private int userId;
    private boolean clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        clicked = false;


        //Test Login Button
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void login(){
        //TODO:Create real login
        if(!clicked) {
            clicked = true;
            System.out.println("Logged in");
            sharedPreferences = getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
            loggedIn = sharedPreferences.getBoolean("loggedInState", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("loggedInState", true);
            editor.putInt("currentUser", userId);
            editor.apply();

            //TODO: Should only show toast if login is successful
            Toast.makeText(Login.this,"Logged in!",
                    Toast.LENGTH_LONG).show();

            Intent loginSuccess = new Intent(this, MainActivity.class);
            loginSuccess.putExtra("fromLoginPage", "loggedIn");
            startActivity(loginSuccess);
            finish();
        }
    }


}

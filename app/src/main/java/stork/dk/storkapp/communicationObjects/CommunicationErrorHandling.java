package stork.dk.storkapp.communicationObjects;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import stork.dk.storkapp.LoginOrSignup;

/**
 * @author morten
 */
public class CommunicationErrorHandling {
    public static void handle403(Activity activity) {
        Intent login = new Intent(activity, LoginOrSignup.class);
        activity.startActivity(login);
        activity.finish();
        Toast.makeText(activity, "Error connecting to server.", Toast.LENGTH_LONG).show();
    }
}

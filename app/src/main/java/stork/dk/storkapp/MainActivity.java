package stork.dk.storkapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import stork.dk.storkapp.communicationObjects.Constants;

/**
 * @author Mathias, Johannes, Morten
 */
public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPrefs;
    private int userId;
    private boolean loggedIn;
    private String sessionId;

    private MapOverviewFragment mapOverviewFragment;
    private FriendsFragment friendsFragment;
    private GroupsFragment groupsFragment;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = getApplicationContext().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        loggedIn = sharedPrefs.getBoolean(Constants.LOGGED_IN_KEY, false);
        userId = sharedPrefs.getInt(Constants.CURRENT_USER_KEY, 0);
        sessionId = sharedPrefs.getString(Constants.CURRENT_SESSION_KEY, "");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        checkIfLoggedIn();
        super.onResume();
    }

    @Override
    public void onRestart() {
        checkIfLoggedIn();
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, Settings.class);
            startActivity(settings);
        }
        if (id == R.id.action_logout) {
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle args;
            switch (position) {
                case 0:
                    mapOverviewFragment = new MapOverviewFragment();
                    args = new Bundle();
                    args.putInt(Constants.CURRENT_USER_KEY, userId);
                    args.putString(Constants.CURRENT_SESSION_KEY, sessionId);
                    mapOverviewFragment.setArguments(args);
                    return mapOverviewFragment;
                case 1:
                    friendsFragment = new FriendsFragment();
                    args = new Bundle();
                    args.putInt(Constants.CURRENT_USER_KEY, userId);
                    args.putString(Constants.CURRENT_SESSION_KEY, sessionId);
                    friendsFragment.setArguments(args);
                    return friendsFragment;
                case 2:
                    groupsFragment = new GroupsFragment();
                    args = new Bundle();
                    args.putInt(Constants.CURRENT_USER_KEY, userId);
                    args.putString(Constants.CURRENT_SESSION_KEY, sessionId);
                    groupsFragment.setArguments(args);
                    return groupsFragment;
                default:
                    return PlaceholderFragment.newInstance(position + 1);

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void checkIfLoggedIn() {
        sharedPrefs = getApplicationContext().getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        loggedIn = sharedPrefs.getBoolean(Constants.LOGGED_IN_KEY, false);
        if (!loggedIn) {
            Intent intent = new Intent(this, LoginOrSignup.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void logOut() {
        sharedPrefs = getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        loggedIn = sharedPrefs.getBoolean(Constants.LOGGED_IN_KEY, false);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(Constants.LOGGED_IN_KEY, false);
        editor.apply();

        if (mapOverviewFragment != null) mapOverviewFragment.stopLocationUpdates();

        Toast.makeText(MainActivity.this, "Logged out!",
                Toast.LENGTH_LONG).show();

        onResume();
    }
}

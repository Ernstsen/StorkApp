package stork.dk.storkapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stork.dk.storkapp.friends.Friend;
import stork.dk.storkapp.friends.Group;
import stork.dk.storkapp.friends.Traceable;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * @author Morten Erfurt Hansen
 */
public class MapOverviewFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private View rootView;
    private Spinner findFriendsSpinner;
    private MapView mapView;

    private GoogleMap googleMap;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */

    private LatLng lastPosition;

    private List<Group> groups;
    private List<Friend> friends;

    private void retrieveGroupsAndFriendsFromRESTService() {
        // LAV NOGET LOGIK DER SØRGER FOR AT BRUGEREN IKKE SELV BLIVER SAT SOM EN VEN AF SIG SELV

        // Temporary simulation of retrieval
        stork.dk.storkapp.communicationObjects.helperObjects.Location johannesLocation =
                new stork.dk.storkapp.communicationObjects.helperObjects.Location(56.150312, 10.204725, 0);
        Friend johannes = new Friend(1, "Johannes", johannesLocation);

        stork.dk.storkapp.communicationObjects.helperObjects.Location mortensLocation =
                new stork.dk.storkapp.communicationObjects.helperObjects.Location(56.171096, 10.189839, 0);
        Friend morten = new Friend(2, "Morten", mortensLocation);

        stork.dk.storkapp.communicationObjects.helperObjects.Location mathiasLocation =
                new stork.dk.storkapp.communicationObjects.helperObjects.Location(55.672761, 12.564924, 0);
        Friend mathias = new Friend(3, "Mathias", mathiasLocation);

        friends = new ArrayList<>();
        friends.add(johannes);
        friends.add(morten);
        friends.add(mathias);

        List<Friend> group1sFriends = new ArrayList<>();
        group1sFriends.add(johannes);
        group1sFriends.add(morten);

        Group group1 = new Group(1, "First group", group1sFriends);

        List<Friend> group2sFriends = new ArrayList<>();
        group2sFriends.add(mathias);
        group2sFriends.add(morten);

        Group group2 = new Group(2, "Second group", group2sFriends);

        groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);
    }

    private void populateFriendsSpinner() {
        List<Traceable> spinnerItems = new ArrayList<Traceable>();

        Set<Friend> allFriends = new HashSet();
        for (Group group : groups) {
            allFriends.addAll(group.getFriends());
        }

        Group groupContainingAllFriends = new Group(0, "Show all", new ArrayList<Friend>(allFriends));
        spinnerItems.add(groupContainingAllFriends);

        for (Group group : groups) {
            spinnerItems.add(group);
            for (Friend friend : group.getFriends()) {
                spinnerItems.add(friend);
            }
        }

        ArrayAdapter<Traceable> adapter = new ArrayAdapter<Traceable>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        findFriendsSpinner.setAdapter(adapter);
    }

    private Map<Integer, Marker> getFriendsMarkers() {
        Map<Integer, Marker> markers = new HashMap<>();

        for (Friend friend : friends) {
            LatLng location3 = new LatLng(friend.getLocation().getLatitude(), friend.getLocation().getLongitude());
            markers.put(friend.getId(), googleMap.addMarker(new MarkerOptions().position(location3).title(friend.getName())));
        }

        return markers;
    }

    private void zoomMap(List<Marker> markers, int mapEdgeOffset) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : markers) {
            if (marker != null) builder.include(marker.getPosition());
        }

        if (lastPosition != null) builder.include(lastPosition);

        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mapEdgeOffset);

        googleMap.moveCamera(cameraUpdate);

        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_overview, container, false);
        findFriendsSpinner = (Spinner) rootView.findViewById(R.id.findFriendsSpinner);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        retrieveGroupsAndFriendsFromRESTService();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                if (mLocationRequest == null) startLocationUpdates();
                setMap();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Trigger new location updates at interval
     *
     * precondition: anywhere this method is called, enabled location permissions should be verified
     */
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    // Retrieves users position
    public void onLocationChanged(Location location) {
        // New location has now been determined
        // For debugging:
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

        lastPosition = new LatLng(location.getLatitude(), location.getLongitude());
    }

    /**
     * Starts the map
     *
     * precondition: anywhere this method is called, enabled location permissions should be verified
     */
    private void setMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                googleMap.setMyLocationEnabled(true);

                populateFriendsSpinner();

                findFriendsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                        // Remove all markers on the map
                        googleMap.clear();

                        Map<Integer, Marker> markers = getFriendsMarkers();

                        List<Marker> markersForMapZoom = new ArrayList<>();

                        Traceable selectedItem = (Traceable) parent.getSelectedItem();

                        if (selectedItem instanceof Group) {
                            Group selectedGroup = (Group) selectedItem;

                            for (Friend friend : selectedGroup.getFriends()) {
                                markersForMapZoom.add(markers.get(friend.getId()));
                            }
                        } else if (selectedItem instanceof Friend) {
                            markersForMapZoom.add(markers.get(selectedItem.getId()));
                        }

                        zoomMap(markersForMapZoom, 200);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        setMap();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
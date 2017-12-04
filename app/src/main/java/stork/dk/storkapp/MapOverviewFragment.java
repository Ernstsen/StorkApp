package stork.dk.storkapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import stork.dk.storkapp.friendsSpinner.Friend;
import stork.dk.storkapp.friendsSpinner.Group;
import stork.dk.storkapp.friendsSpinner.Traceable;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * @author Morten Erfurt Hansen
 */
public class MapOverviewFragment extends Fragment {
    private View rootView;
    private Spinner findFriendsSpinner;
    private MapView mapView;

    private GoogleMap googleMap;
    private LatLng lastKnownPosition;

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

        if (lastKnownPosition != null) builder.include(lastKnownPosition);

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

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        startLocationUpdates();
                        setMap();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        System.exit(0);
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.title_location_permission)
                                .setMessage(R.string.text_location_permission)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        token.continuePermissionRequest();
                                    }
                                })
                                .create()
                                .show();
                    }
                }).check();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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

    private void startLocationUpdates() {
        long trackingInterval = 30 * 1000;  /* 30 secs */
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(trackingInterval);

        SmartLocation.with(getActivity())
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        updateLastKnownPosition(location);
                    }
                });
    }

    public void stopLocationUpdates() {
        SmartLocation.with(getActivity()).location().stop();
    }

    // Retrieves users position
    public void updateLastKnownPosition(Location location) {
        // New location has now been determined
        // For debugging:
        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();*/

        lastKnownPosition = new LatLng(location.getLatitude(), location.getLongitude());
    }

    /**
     * Sets the map
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
}
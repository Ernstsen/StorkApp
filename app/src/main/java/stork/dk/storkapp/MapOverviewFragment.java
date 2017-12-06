package stork.dk.storkapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import stork.dk.storkapp.communicationObjects.CommunicationErrorHandling;
import stork.dk.storkapp.communicationObjects.CommunicationsHandler;
import stork.dk.storkapp.communicationObjects.Constants;
import stork.dk.storkapp.communicationObjects.GroupsResponse;
import stork.dk.storkapp.communicationObjects.UpdateLocationRequest;
import stork.dk.storkapp.friendsSpinner.Friend;
import stork.dk.storkapp.friendsSpinner.Group;
import stork.dk.storkapp.friendsSpinner.Traceable;

/**
 * @author morten
 */
public class MapOverviewFragment extends Fragment implements OnMapReadyCallback {
    private Spinner findFriendsSpinner;
    private MapView mapView;

    private GoogleMap googleMap;
    private LatLng lastKnownPosition;

    private List<Group> groups;
    private List<Friend> friends;
    private Map<Integer, Marker> markers;

    private int userId;
    private String sessionId;

    // Constants for location tracking
    private final LocationAccuracy TRACKING_ACCURACY = LocationAccuracy.HIGH;
    private final long TRACKING_INTERVAL = 60 * 1000;  /* 60 secs */
    private final float TRACKING_DISTANCE = 0;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_overview, container, false);
        findFriendsSpinner = (Spinner) rootView.findViewById(R.id.findFriendsSpinner);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLocationUpdates();
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
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(TRACKING_ACCURACY)
                .setDistance(TRACKING_DISTANCE)
                .setInterval(TRACKING_INTERVAL);

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
    private void updateLastKnownPosition(Location location) {

        lastKnownPosition = new LatLng(location.getLatitude(), location.getLongitude());

        updateUserLocationAtRestService(location);
    }

    private void updateUserLocationAtRestService(Location location) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        long now = timestamp.getTime();

        stork.dk.storkapp.communicationObjects.helperObjects.Location locationToUpload
                = new stork.dk.storkapp.communicationObjects.helperObjects.Location(location.getLatitude(), location.getLongitude(), now);

        UpdateLocationRequest updateLocationRequest = new UpdateLocationRequest(userId, sessionId, locationToUpload);

        CommunicationsHandler.updateLocation(getActivity(), updateLocationRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Toast.makeText(getActivity(), "onSuccess - statusCode: " + statusCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 403){
                    CommunicationErrorHandling.handle403(getActivity());
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        createPermissionListener();

        retrieveGroupsAndFriendsFromRESTService();
    }

    private void placeMarkersOnMap(List<Friend> friends) {
        // clear existing markers
        googleMap.clear();

        markers = new HashMap<>();

        for (Friend friend : friends) {
            LatLng location = new LatLng(friend.getLocation().getLatitude(), friend.getLocation().getLongitude());
            markers.put(friend.getId(), googleMap.addMarker(new MarkerOptions().position(location).title(friend.getName())));
        }
    }

    private void zoomMap(List<Marker> markers, int mapEdgeOffset) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : markers) {
            if(marker.getPosition() != null) builder.include(marker.getPosition());
        }

        if (lastKnownPosition != null) builder.include(lastKnownPosition);

        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mapEdgeOffset);

        googleMap.moveCamera(cameraUpdate);

        googleMap.animateCamera(cameraUpdate);
    }

    private void createPermissionListener() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        googleMap.setMyLocationEnabled(true);
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
                                .setCancelable(false)
                                .create()
                                .show();
                    }
                }).check();
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

    private void friendsSpinnerOnItemSelectedListener() {
        findFriendsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
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

                if (!markersForMapZoom.isEmpty()) zoomMap(markersForMapZoom, 200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void retrieveGroupsAndFriendsFromRESTService() {
        // Temporary simulation of retrieval
        /*stork.dk.storkapp.communicationObjects.helperObjects.Location johannesLocation =
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
        groups.add(group2);*/

        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        groups = new ArrayList<>();
        friends = new ArrayList<>();

        CommunicationsHandler.getGroups(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GroupsResponse resp = new Gson().fromJson(new String(responseBody), GroupsResponse.class);

                //Toast.makeText(getActivity(), new String(responseBody), Toast.LENGTH_LONG).show();

                for (Group group : resp.getGroups()) {
                    if (!group.getFriends().isEmpty()) {
                        groups.add(group);
                        for (Friend friend : group.getFriends()) {
                            friends.add(friend);
                        }
                    }
                }

                placeMarkersOnMap(friends);

                populateFriendsSpinner();

                friendsSpinnerOnItemSelectedListener();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 403){
                    CommunicationErrorHandling.handle403(getActivity());
                }
            }
        });
    }
}
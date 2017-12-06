package stork.dk.storkapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.sql.Time;
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
public class MapOverviewFragment extends Fragment {
    private Spinner findFriendsSpinner;
    private MapView mapView;

    private GoogleMap googleMap;
    private LatLng lastKnownPosition;

    private List<Group> groups;
    private List<Friend> friends;
    private Map<Integer, Marker> markers;

    private int userId;
    private String sessionId;

    private Handler requestServerAtIntervalHandler = new Handler();
    private int REQUEST_SERVER_INTERVAL = 60 * 1000; // 60 secs
    private int REQUEST_SERVER_INTERVAL_ACTIVE = 1;

    // Constants for location tracking
    private final LocationAccuracy TRACKING_ACCURACY = LocationAccuracy.HIGH;
    private final long TRACKING_INTERVAL = 60 * 1000;  // 60 secs
    private final float TRACKING_DISTANCE = 0;

    private final Double INCLUDE_USER_IN_ZOOM_DISTANCE = 5000.0; // 5 km

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_overview, container, false);
        findFriendsSpinner = (Spinner) rootView.findViewById(R.id.findFriendsSpinner);

        Bundle args = getArguments();
        userId = args.getInt(Constants.CURRENT_USER_KEY);
        sessionId = args.getString(Constants.CURRENT_SESSION_KEY);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                startLocationUpdates();
                createPermissionListener();
                startRequestingServerOnInterval();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if(requestServerAtIntervalHandler.hasMessages(REQUEST_SERVER_INTERVAL_ACTIVE)) {
            startRequestingServerOnInterval();
        }
        Log.d("THEAPP", "onResume() CALLED");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        stopRequestingServerOnInterval();
        Log.d("THEAPP", "onPause() CALLED");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("THEAPP", "onStop() CALLED");
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
        long now = Utility.timestamp.getTime();

        stork.dk.storkapp.communicationObjects.helperObjects.Location locationToUpload
                = new stork.dk.storkapp.communicationObjects.helperObjects.Location(location.getLatitude(), location.getLongitude(), now);

        UpdateLocationRequest updateLocationRequest = new UpdateLocationRequest(userId, sessionId, locationToUpload);

        CommunicationsHandler.updateLocation(getActivity(), updateLocationRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(statusCode == 403){
                    CommunicationErrorHandling.handle403(getActivity());
                }
            }
        });
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

        builder = addMarkersToBoundsBuilder(builder, markers, lastKnownPosition);

        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mapEdgeOffset);

        googleMap.moveCamera(cameraUpdate);

        googleMap.animateCamera(cameraUpdate);
    }

    private LatLngBounds.Builder addMarkersToBoundsBuilder(LatLngBounds.Builder builder, List<Marker> markers, LatLng usersPosition) {
        boolean usersPositionExists = usersPosition != null;
        Double shortestDistanceToUser = null;

        if (usersPositionExists) {
            for (Marker marker : markers) {
                boolean markerPositionExists = marker.getPosition() != null;
                if (markerPositionExists) {
                    Double markersDistanceToUser = SphericalUtil.computeDistanceBetween(usersPosition, marker.getPosition());
                    if (shortestDistanceToUser == null || markersDistanceToUser < shortestDistanceToUser) {
                        shortestDistanceToUser = markersDistanceToUser;
                    }
                    builder.include(marker.getPosition());
                }
            }
            if (shortestDistanceToUser < INCLUDE_USER_IN_ZOOM_DISTANCE)
            builder.include(lastKnownPosition);
        } else {
            for (Marker marker : markers) {
                if(marker.getPosition() != null) builder.include(marker.getPosition());
            }
        }
        return builder;
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
        HashMap<String, String> params = new HashMap<>();
        params.put("sessionId", sessionId);
        params.put("userId", String.valueOf(userId));

        groups = new ArrayList<>();
        friends = new ArrayList<>();

        CommunicationsHandler.getGroups(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                GroupsResponse resp = new Gson().fromJson(new String(responseBody), GroupsResponse.class);

                Log.d("THEAPP", new String(responseBody));

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

    private void startRequestingServerOnInterval() {
        requestServerAtIntervalHandlerTask.run();
    }

    private void stopRequestingServerOnInterval() {
        Log.d("THEAPP", "requestServerAtIntervalHandlerTask stopped");
        if (requestServerAtIntervalHandler.hasMessages(REQUEST_SERVER_INTERVAL_ACTIVE)) {
            requestServerAtIntervalHandler.removeMessages(REQUEST_SERVER_INTERVAL_ACTIVE);
        }
        requestServerAtIntervalHandler.removeCallbacks(requestServerAtIntervalHandlerTask);
    }

    Runnable requestServerAtIntervalHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            Log.d("THEAPP", "requestServerAtIntervalHandlerTask started");
            requestServerAtIntervalHandler.sendEmptyMessage(REQUEST_SERVER_INTERVAL_ACTIVE);//Do this when you add the call back.
            retrieveGroupsAndFriendsFromRESTService();
            requestServerAtIntervalHandler.postDelayed(requestServerAtIntervalHandlerTask, REQUEST_SERVER_INTERVAL);
        }
    };
}
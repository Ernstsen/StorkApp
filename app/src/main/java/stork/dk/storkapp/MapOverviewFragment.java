package stork.dk.storkapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Morten Erfurt Hansen
 */
public class MapOverviewFragment extends Fragment {
    private View rootView;
    private Spinner findFriendsSpinner;
    private MapView mapView;
    private GoogleMap googleMap;

    private void populateFriendsSpinner(List<Marker> markers) {
        List<String> markerNames =  new ArrayList<String>();

        for (Marker marker : markers) {
            markerNames.add(marker.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, markerNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        findFriendsSpinner.setAdapter(adapter);
    }

    private List<Marker> populateMarkerList() {
        googleMap.clear();

        LatLng sydney = new LatLng(-34, 151);
        Marker marker1 = googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker 1").snippet("Marker Description 1"));

        LatLng sydney2 = new LatLng(-34, 152);
        Marker marker2 = googleMap.addMarker(new MarkerOptions().position(sydney2).title("Marker 2").snippet("Marker Description 2"));

        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(marker1);
        markers.add(marker2);

        return markers;
    }

    private void zoomMap(List<Marker> markers, int mapEdgeOffset) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, mapEdgeOffset);

        googleMap.moveCamera(cameraUpdate);

        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_overview, container, false);
        findFriendsSpinner = (Spinner) rootView.findViewById(R.id.findFriendsSpinner);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                // For showing a move to my location button
                // TODO: Add permission handling
                //googleMap.setMyLocationEnabled(true);

                final List<Marker> markers = populateMarkerList();

                populateFriendsSpinner(markers);

                zoomMap(markers, 50);

//                findFriendsSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        // Only handles a single friend
//                        // Needs to add support for group
//                        //zoomMap(Collections.singletonList(markers.get(position)), 0);
//                    }
//                });

            }
        });

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
}
package stork.dk.storkapp;

import android.location.Location;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author morten
 */
public class StandardLocationSource implements LocationSource {
    private OnLocationChangedListener onLocationChangedListener;
    private boolean paused;

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
    }

    public void setLocation(LatLng point) {
        if (onLocationChangedListener != null && !paused) {
            Location location = new Location("StandardLocationSource");
            location.setLatitude(point.latitude);
            location.setLongitude(point.longitude);
            location.setAccuracy(10);
            onLocationChangedListener.onLocationChanged(location);
        }
    }

    public void onResume() {
        paused = false;
    }

    public void onPause() {
        paused = true;
    }
}

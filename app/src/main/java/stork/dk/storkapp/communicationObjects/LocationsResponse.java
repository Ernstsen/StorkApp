package stork.dk.storkapp.communicationObjects;

import java.io.Serializable;
import java.util.Map;

import stork.dk.storkapp.communicationObjects.helperObjects.Location;

/**
 * @author Johannes on 30-11-2017.
 */

public class LocationsResponse implements Serializable {
    private Map<String, Location> locations;

    public LocationsResponse() {
    }

    public LocationsResponse(Map<String, Location> locations) {
        this.locations = locations;
    }

    public Map<String, Location> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, Location> locations) {
        this.locations = locations;
    }
}

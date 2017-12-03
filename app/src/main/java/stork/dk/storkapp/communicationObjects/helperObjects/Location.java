package stork.dk.storkapp.communicationObjects.helperObjects;

import java.io.Serializable;

/**
 * @author Johannes on 30-11-2017.
 */

public class Location implements Serializable {
    private double longitude;
    private double latitude;
    private long timeStamp;

    public Location() {
    }

    public Location(double latitude, double longitude, long timeStamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timeStamp = timeStamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}

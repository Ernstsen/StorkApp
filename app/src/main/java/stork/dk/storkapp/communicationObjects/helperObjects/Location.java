package stork.dk.storkapp.communicationObjects.helperObjects;

import java.io.Serializable;

/**
 * @author Johannes on 30-11-2017.
 */

public class Location implements Serializable {
    private double longtitude;
    private double lattitude;
    private long timeStamp;

    public Location() {
    }

    public Location(double longtitude, double lattitude, long timeStamp) {
        this.longtitude = longtitude;
        this.lattitude = lattitude;
        this.timeStamp = timeStamp;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}

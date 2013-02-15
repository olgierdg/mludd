package edu.mbryla.andlogger.database.models;

import java.sql.Timestamp;

import edu.mbryla.andlogger.database.DatabaseRow;


/**
 * @author mateusz
 */
public class LocationLog implements DatabaseRow {
    private long id;
    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private Double altitude = null;
    private Float accuracy = null;
    private Float speed = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("LocationLog: ");

        buf.append(id).append(", ").append(timestamp).append(", ")
                .append(latitude).append(", ").append(longitude).append(", ")
                .append(altitude).append(", ").append(accuracy).append(", ")
                .append(speed).append(";");

        return buf.toString();
    }

}

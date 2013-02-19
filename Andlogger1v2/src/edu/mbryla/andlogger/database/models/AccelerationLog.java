package edu.mbryla.andlogger.database.models;

import java.sql.Timestamp;

import edu.mbryla.andlogger.database.DatabaseRow;


public class AccelerationLog implements DatabaseRow, DbLog {
    private long id;
    private Timestamp timestamp;
    private float x, y, z;

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("Acceleration: ");

        buf.append(id).append(", ").append(timestamp).append(", ").append(x)
                .append(", ").append(y).append(", ").append(z).append(";");

        return buf.toString();
    }
}

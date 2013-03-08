/**
 *
 */
package edu.mbryla.andlogger.database.dao;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.mbryla.andlogger.database.DAOHelper;
import edu.mbryla.andlogger.database.Database;
import edu.mbryla.andlogger.database.models.LocationLog;
import edu.mbryla.andlogger.database.query.QueryBuilder;
import edu.mbryla.andlogger.database.query.Type;


/**
 * @author mateusz
 */
public class LocationLogDAO extends DAOHelper<LocationLog> {
    public final static String TABLE_NAME = "locationlog";

    // database columns
    private static final String TABLE_TIMESTAMP = "timestamp";
    private static final String TABLE_LATITUDE = "latitude";
    private static final String TABLE_LONGITUDE = "longitude";
    private static final String TABLE_ALTITUDE = "altitude";
    private static final String TABLE_ACCURACCY = "accuraccy";
    private static final String TABLE_SPEED = "speed";

    // schema
    private static final String TABLE_CREATE = QueryBuilder.createTable(TABLE_NAME)
            .addPrimaryKey()
            .addColumn(TABLE_TIMESTAMP, Type.TIMESTAMP, Type.Nullable.NOTNULL)
            .addColumn(TABLE_LATITUDE, Type.REAL, Type.Nullable.NOTNULL)
            .addColumn(TABLE_LONGITUDE, Type.REAL, Type.Nullable.NOTNULL)
            .addColumn(TABLE_ALTITUDE, Type.REAL, Type.Nullable.NULL)
            .addColumn(TABLE_ACCURACCY, Type.REAL, Type.Nullable.NULL)
            .addColumn(TABLE_SPEED, Type.REAL, Type.Nullable.NULL)
            .build();

    public LocationLogDAO(Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    /* (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAOHelper#getTableSchema()
     */
    @Override
    protected String getTableSchema() {
        return TABLE_CREATE;
    }

    /* (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAOHelper#getTableName()
     */
    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    /* (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAOHelper#prepareContentValues(edu.mbryla.andlogger.database.DatabaseRow)
     */
    @Override
    protected ContentValues prepareContentValues(LocationLog item) {
        ContentValues values = new ContentValues();
        values.put(TABLE_TIMESTAMP, item.getTimestamp().toString());
        values.put(TABLE_LATITUDE, item.getLatitude());
        values.put(TABLE_LONGITUDE, item.getLongitude());
        values.put(TABLE_ALTITUDE, item.getAltitude());
        values.put(TABLE_ACCURACCY, item.getAccuracy());
        values.put(TABLE_SPEED, item.getSpeed());
        return values;
    }

    /* (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAOHelper#readItem(android.database.Cursor)
     */
    @Override
    protected LocationLog readItem(Cursor cursor) {
        LocationLog loc = new LocationLog();

        loc.setId(cursor.getLong(0));
        loc.setTimestamp(Timestamp.valueOf(cursor.getString(1)));
        loc.setLatitude(cursor.getDouble(2));
        loc.setLongitude(cursor.getDouble(3));
        if (!cursor.isNull(4)) {
            loc.setAltitude(cursor.getDouble(4));
        }
        if (!cursor.isNull(5)) {
            loc.setAccuracy(cursor.getFloat(5));
        }
        if (!cursor.isNull(6)) {
            loc.setSpeed(cursor.getFloat(6));
        }

        return loc;
    }
}

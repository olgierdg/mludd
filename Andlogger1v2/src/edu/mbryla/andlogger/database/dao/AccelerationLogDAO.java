package edu.mbryla.andlogger.database.dao;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import edu.mbryla.andlogger.database.DAOHelper;
import edu.mbryla.andlogger.database.Database;
import edu.mbryla.andlogger.database.models.AccelerationLog;
import edu.mbryla.andlogger.database.query.QueryBuilder;
import edu.mbryla.andlogger.database.query.Type;

public class AccelerationLogDAO extends DAOHelper<AccelerationLog> {
    public static final String TABLE_NAME = "accelerationlog";

    // columns
    private static final String TABLE_TIMESTAMP = "timestamp";
    private static final String TABLE_X = "x";
    private static final String TABLE_Y = "y";
    private static final String TABLE_Z = "z";

    // schema
    private static final String TABLE_CREATE = QueryBuilder.createTable(TABLE_NAME)
            .addPrimaryKey()
            .addColumn(TABLE_TIMESTAMP, Type.TIMESTAMP, Type.Nullable.NOTNULL)
            .addColumn(TABLE_X, Type.REAL, Type.Nullable.NOTNULL)
            .addColumn(TABLE_Y, Type.REAL, Type.Nullable.NOTNULL)
            .addColumn(TABLE_Z, Type.REAL, Type.Nullable.NOTNULL)
            .build();

    public AccelerationLogDAO(Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    @Override
    protected String getTableSchema() {
        return TABLE_CREATE;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected ContentValues prepareContentValues(AccelerationLog item) {
        ContentValues values = new ContentValues();
        values.put(TABLE_TIMESTAMP, item.getTimestamp().toString());
        values.put(TABLE_X, item.getX());
        values.put(TABLE_Y, item.getY());
        values.put(TABLE_Z, item.getZ());
        return values;
    }

    @Override
    protected AccelerationLog readItem(Cursor cursor) {
        AccelerationLog ret = new AccelerationLog();

        ret.setId(cursor.getLong(0));
        ret.setTimestamp(Timestamp.valueOf(cursor.getString(1)));
        ret.setX(cursor.getFloat(2));
        ret.setY(cursor.getFloat(3));
        ret.setZ(cursor.getFloat(4));

        return ret;
    }

}

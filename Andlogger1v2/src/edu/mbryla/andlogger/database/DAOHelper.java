package edu.mbryla.andlogger.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.mbryla.andlogger.database.query.QueryBuilder;


/**
 * A helper class for implementing DAOs.
 *
 * @author mateusz
 * @param <T>
 *            Type of the object stored in the database. Must extend
 *            DatabaseRow.
 */
public abstract class DAOHelper<T extends DatabaseRow> extends SQLiteOpenHelper
        implements DAO<T> {
    public static final String SQLITE_ROWID = "_ROWID_";

    public DAOHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dupa", "created table " + getTableName());
        db.execSQL(getTableSchema());
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QueryBuilder.dropTable(getTableName()));
        onCreate(db);
    }

    /*
     * (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAO#save(java.lang.Object)
     */
    @Override
    public void save(T item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = prepareContentValues(item);

        db.insert(getTableName(), null, values);
        db.close();
    }

    /*
     * (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAO#load(long)
     */
    @Override
    public T load(long id) {
        T item = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(getTableName(), null, SQLITE_ROWID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            item = readItem(cursor);
        }
        cursor.close();

        return item;
    }

    /*
     * (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAO#loadAll()
     */
    @Override
    public List<T> loadAll() {
        List<T> all = new ArrayList<T>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QueryBuilder.selectAll(getTableName()),
                null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                all.add(readItem(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return all;
    }

    /*
     * (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAO#update(java.lang.Object)
     */
    @Override
    public int update(T item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = prepareContentValues(item);

        int updated = db.update(getTableName(), values, SQLITE_ROWID + "=?",
                new String[] { String.valueOf(item.getId()) });
        db.close();

        return updated;
    }

    /*
     * (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAO#delete(java.lang.Object)
     */
    @Override
    public void delete(T item) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(getTableName(), SQLITE_ROWID + "=?",
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }

    /*
     * (non-Javadoc)
     * @see edu.mbryla.andlogger.database.DAO#count()
     */
    @Override
    public int count() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(QueryBuilder.selectAll(getTableName()),
                null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    /**
     * Returns a string containing a "CREATE TABLE ..." query.
     *
     * @return A schema string.
     */
    protected abstract String getTableSchema();

    /**
     * Returns the name of the table.
     *
     * @return Table name.
     */
    protected abstract String getTableName();

    /**
     * Converts the object to the ContentValues representation.
     *
     * @param item
     *            An object of type T.
     * @return ContentValues object.
     */
    protected abstract ContentValues prepareContentValues(T item);

    /**
     * Parses a datatable row to an object.
     *
     * @param cursor
     *            Datatable cursor, pointing to a row.
     * @return An object.
     */
    protected abstract T readItem(Cursor cursor);

}

package edu.mbryla.andlogger.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import edu.mbryla.andlogger.database.query.QueryBuilder;


/**
 * @author mateusz
 */
public abstract class DAOHelper<T extends DatabaseRow> extends
        SQLiteOpenHelper implements DAO<T> {
    public static final String SQLITE_ROWID = "_ROWID_";
    public static final String KEY_FIELD = SQLITE_ROWID + " " + Type.KEY;

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

    @Override
    public void save(T item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = prepareContentValues(item);

        db.insert(getTableName(), null, values);
        db.close();
    }

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

        return item;
    }

    @Override
    public List<T> loadAll() {
        List<T> all = new ArrayList<T>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QueryBuilder.dropTable(getTableName()) + getTableName() + ";", null);

        if (cursor.moveToFirst()) {
            do {
                all.add(readItem(cursor));
            } while (cursor.moveToNext());
        }

        return all;
    }

    @Override
    public int update(T item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = prepareContentValues(item);

        return db.update(getTableName(), values, SQLITE_ROWID + "=?",
                new String[] { String.valueOf(item.getId()) });
    }

    @Override
    public void delete(T item) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(getTableName(), SQLITE_ROWID + "=?",
                new String[] { String.valueOf(item.getId()) });
    }

    @Override
    public int count() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(QueryBuilder.count(getTableName()) + getTableName() + ";", null);
        return cursor.getInt(0);
    }

    protected abstract String getTableSchema();

    protected abstract String getTableName();

    protected abstract ContentValues prepareContentValues(T item);

    protected abstract T readItem(Cursor cursor);

}

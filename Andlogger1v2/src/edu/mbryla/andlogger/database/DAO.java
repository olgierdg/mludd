package edu.mbryla.andlogger.database;

import java.util.List;


/**
 * Simple Data Access Object interfase.
 *
 * @author mateusz
 * @param <T>
 *            Type.
 */
public interface DAO<T> {
    /**
     * Saves an item into the database.
     *
     * @param item
     *            An object.
     */
    void save(T item);

    /**
     * Loads an object with the given id from the database.
     *
     * @param id
     *            Id of the item in the database.
     * @return An object.
     */
    T load(long id);

    /**
     * Loads all objects from the database.
     *
     * @return A list of all elements in the database.
     */
    List<T> loadAll();

    /**
     * Updates an object.
     *
     * @param item
     *            Object to update.
     * @return Number of updated rows.
     */
    int update(T item);

    /**
     * Removes an object from the database.
     *
     * @param item
     *            Object to remove.
     */
    void delete(T item);

    /**
     * Returns the count of all rows in the database.
     *
     * @return Number of all rows.
     */
    int count();
}

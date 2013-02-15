package edu.mbryla.andlogger.database;

import java.util.List;


/**
 * @author mateusz
 */
public interface DAO<T> {
    void save(T item);

    T load(long id);

    List<T> loadAll();

    int update(T item);

    void delete(T item);

    int count();
}

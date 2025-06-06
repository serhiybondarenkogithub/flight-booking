package org.example.dao.storage;

import org.example.exception.StorageException;

import java.util.Map;

public interface MapStorage<T> {
    Map<String, T> loadData() throws StorageException;
    void saveData(Map<String, T> data) throws StorageException;
}

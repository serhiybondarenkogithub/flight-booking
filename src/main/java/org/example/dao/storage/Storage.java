package org.example.dao.storage;

import org.example.exception.StorageException;

import java.util.List;

public interface Storage<T> {
    List<T> loadData() throws StorageException;
    void saveData(List<T> data) throws StorageException;
}

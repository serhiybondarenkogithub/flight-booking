package org.example.dao.storage;

import org.example.exception.StorageException;

import java.util.Collection;
import java.util.List;

public interface ListStorage<T> {
    List<T> loadData() throws StorageException;
    void saveData(List<T> data) throws StorageException;
}

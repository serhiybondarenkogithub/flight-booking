package org.example.storage;

import org.example.exception.StorageException;

import java.io.*;
import java.util.List;

public class FileToListStorage<T> implements Storage<T> {
    private final File file;

    public FileToListStorage(File file) {
        if (file == null) throw new IllegalArgumentException("File cannot be null");
        this.file = file;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> loadData() throws StorageException {
        if (!file.exists()) {
            throw new StorageException("File not found: " + file.getName());
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof List<?> list) {
                return (List<T>) list;
            }
            else throw new StorageException("Invalid data format in file");

        } catch (IOException | ClassNotFoundException e) {
            throw new StorageException("Failed to load data from file", e);
        }
    }
    @Override
    public void saveData(List<T> data) throws StorageException {
        if (data == null) {
            throw new StorageException("Data to save cannot be null");
        }

        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            throw new StorageException("Failed to save data to file: " + file.getName(), e);
        }
    }
}

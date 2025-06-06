package org.example.dao;

import org.example.dao.storage.ListStorage;
import org.example.dao.storage.MapStorage;
import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.interfaces.Identifiable;

import java.util.*;
import java.util.stream.IntStream;

public abstract class AbstractMapDao<T extends Identifiable> implements MapDao<T> {
    protected final MapStorage<T> storage;
    protected final Class<T> entityClass;

    protected AbstractMapDao(MapStorage<T> storage, Class<T> entityClass) {
        this.storage = storage;
        this.entityClass = entityClass;
    }

    protected Map<String, T> safeLoad() throws DaoException {
        try {
            Map<String, T> data = storage.loadData();
            if (data == null) throw new DaoException(getClass().getSimpleName() + ": null data returned from storage");
            return data;
        } catch (StorageException e) {
            throw new DaoException(getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    protected void safeSave(Map<String, T> data) throws DaoException {
        try {
            storage.saveData(data);
        } catch (StorageException e) {
            throw new DaoException(getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void createAll(Map<String, T> entities) throws DaoException {
        if (entities == null) {
            throw new DaoException(entityClass.getSimpleName() + " map is null");
        }
        if (entities.containsValue(null)) {
            throw new DaoException("Map contains null value");
        }

        Map<String, T> current = safeLoad();
        current.putAll(entities);
        safeSave(current);
    }

    @Override
    public void create(String id, T entity) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot create " + entityClass.getSimpleName() + ": id is null or blank");
        }
        if (entity == null) {
            throw new DaoException("Cannot create null " + entityClass.getSimpleName());
        }

        Map<String, T> current = safeLoad();
        current.put(id, entity);
        safeSave(current);
    }

    @Override
    public Optional<T> read(String id) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot read " + entityClass.getSimpleName() + ": id is null or blank");
        }

        Map<String, T> current = safeLoad();
        return Optional.ofNullable(current.get(id));
    }

    @Override
    public void update(String id, T entity) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot update " + entityClass.getSimpleName() + ": id is null or blank");
        }
        if (entity == null) {
            throw new DaoException("Cannot update null " + entityClass.getSimpleName());
        }

        Map<String, T> current = safeLoad();
        if (!current.containsKey(id)) {
            throw new DaoException(entityClass.getSimpleName() + " with id " + id + " not found");
        }

        current.put(id, entity);
        safeSave(current);
    }

    @Override
    public void delete(String id) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot delete " + entityClass.getSimpleName() + ": id is null or blank");
        }

        Map<String, T> current = safeLoad();
        if (current.remove(id) == null) {
            throw new DaoException(entityClass.getSimpleName() + " with id " + id + " not found");
        }

        safeSave(current);
    }

    @Override
    public Map<String, T> readAll() throws DaoException {
        return Collections.unmodifiableMap(safeLoad());
    }
}
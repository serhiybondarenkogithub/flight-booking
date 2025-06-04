package org.example.dao;

import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.interfaces.Identifiable;
import org.example.dao.storage.ListStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public abstract class AbstractDao<T extends Identifiable> implements Dao<T> {
    protected final ListStorage<T> storage;
    protected final Class<T> entityClass;

    protected AbstractDao(ListStorage<T> storage, Class<T> entityClass) {
        this.storage = storage;
        this.entityClass = entityClass;
    }

    protected List<T> safeLoad() throws DaoException {
        try {
            List<T> data = storage.loadData();
            if (data == null) throw new DaoException(this.getClass().getSimpleName() + ": null data returned from storage");
            return data;
        }
        catch (StorageException e) { throw new DaoException(this.getClass().getSimpleName() + ": " + e.getMessage(), e); }
    }

    protected void safeSave(List<T> data) throws DaoException {
        try { storage.saveData(data); }
        catch (StorageException e) { throw new DaoException(this.getClass().getSimpleName() + ": " + e.getMessage(), e); }
    }

    @Override
    public void createAll(List<T> entities) throws DaoException {
        if (entities == null) {
            throw new DaoException(entityClass.getSimpleName() + " list is null");
        }
        OptionalInt nullIndex = IntStream.range(0, entities.size())
                .filter(i -> entities.get(i) == null)
                .findFirst();

        if (nullIndex.isPresent()) { throw new DaoException("List contains null at index " + nullIndex.getAsInt()); }

        List<T> list = safeLoad();
        list.addAll(entities);
        safeSave(list);
    }

    @Override
    public void create(T entity) throws DaoException {
        if (entity == null) throw new DaoException(entityClass.getSimpleName() + " to create is null");
        List<T> list = safeLoad();
        list.add(entity);
        safeSave(list);
    }

    @Override
    public Optional<T> read(String id) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot read " + entityClass.getSimpleName() + ": id is null or blank");
        }
        return safeLoad().stream()
                .filter(obj -> obj.getId().equals(id))
                .findFirst();
    }

    @Override
    public void update(T entity) throws DaoException {
        if (entity == null) throw new DaoException("Cannot update null "+ entityClass.getSimpleName());
        List<T> list = safeLoad();
        OptionalInt indexOpt = IntStream.range(0, list.size())
                .filter(i -> list.get(i).getId().equals(entity.getId()))
                .findFirst();

        if (indexOpt.isPresent()) {
            list.set(indexOpt.getAsInt(), entity);
        } else {
            throw new DaoException(entityClass.getSimpleName() + " with id " + entity.getId() + " not found");
        }
        safeSave(list);
    }

    @Override
    public void delete(String id) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot delete " + entityClass.getSimpleName() + ": id is null or blank");
        }
        List<T> list = safeLoad();
        boolean removed = list.removeIf(obj -> obj.getId().equals(id));
        if (!removed) {
            throw new DaoException(entityClass.getSimpleName() + " with id " + id + " not found");
        }
        safeSave(list);
    }

    @Override
    public List<T> readAll() throws DaoException {
        return Collections.unmodifiableList(safeLoad());
    }
}
package org.example.dao;

import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.example.model.interfaces.Identifiable;
import org.example.dao.storage.ListStorage;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T extends Identifiable> implements Dao<T> {
    protected final ListStorage<T> storage;
    protected final Class<T> entityClass;

    protected AbstractDao(ListStorage<T> storage, Class<T> entityClass) {
        this.storage = storage;
        this.entityClass = entityClass;
    }

    protected List<T> safeLoad() throws DaoException {
        try { return storage.loadData(); }
        catch (StorageException e) { throw new DaoException(this.getClass().getSimpleName() + ": " + e.getMessage(), e); }
    }

    protected void safeSave(List<T> data) throws DaoException {
        try { storage.saveData(data); }
        catch (StorageException e) { throw new DaoException(this.getClass().getSimpleName() + ": " + e.getMessage(), e); }
    }

    @Override
    public void createAll(List<T> entities) throws DaoException {
        if (entities == null || entities.contains(null)) {
            throw new DaoException(entityClass.getSimpleName() + " list is null or contains null");
        }
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
        boolean updated = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(entity.getId())) {
                list.set(i, entity);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new DaoException(entityClass.getSimpleName() + " with id " + entity.getId() + " not found");
        }
        safeSave(list);
    }

    @Override
    public void delete(String id) throws DaoException {
        if (id == null) throw new DaoException("Cannot delete " + entityClass.getSimpleName() + ": id is null");
        List<T> list = safeLoad();
        boolean removed = list.removeIf(obj -> obj.getId().equals(id));
        if (!removed) {
            throw new DaoException(entityClass.getSimpleName() + " with id " + id + " not found");
        }
        safeSave(list);
    }

    @Override
    public List<T> readAll() throws DaoException {
        return safeLoad();
    }
}
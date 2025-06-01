package org.example.dao;

import org.example.exception.DaoException;
import org.example.model.interfaces.Identifiable;

import java.util.List;
import java.util.Optional;

public interface Dao<T extends Identifiable> {
    void createAll(List<T> entities, Class<T> clazz) throws DaoException;

    void create(T entity, Class<T> clazz) throws DaoException;

    Optional<T> read(String id, Class<T> clazz) throws DaoException;

    void update(T entity, Class<T> clazz) throws DaoException;

    void delete(String id, Class<T> clazz) throws DaoException;

    List<T> readAll() throws DaoException;
}
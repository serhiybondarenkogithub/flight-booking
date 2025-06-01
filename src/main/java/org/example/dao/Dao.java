package org.example.dao;

import org.example.exception.DaoException;
import org.example.model.interfaces.Identifiable;

import java.util.List;
import java.util.Optional;

public interface Dao<T extends Identifiable> {
    void createAll(List<T> entities) throws DaoException;

    void create(T entity) throws DaoException;

    Optional<T> read(String id) throws DaoException;

    void update(T entity) throws DaoException;

    void delete(String id) throws DaoException;

    List<T> readAll() throws DaoException;
}
package org.example.dao;

import org.example.exception.DaoException;
import org.example.model.interfaces.Identifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MapDao<T extends Identifiable> {
    void createAll(Map<String, T> entities) throws DaoException;

    void create(String id, T entity) throws DaoException;

    Optional<T> read(String id) throws DaoException;

    void update(String id, T entity) throws DaoException;

    void delete(String id) throws DaoException;

    Map<String, T> readAll() throws DaoException;
}
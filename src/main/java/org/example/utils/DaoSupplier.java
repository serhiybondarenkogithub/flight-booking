package org.example.utils;

import org.example.exception.DaoException;
import org.example.exception.ServiceException;

@FunctionalInterface
public interface DaoSupplier<T> {
    T get() throws DaoException, ServiceException;
}

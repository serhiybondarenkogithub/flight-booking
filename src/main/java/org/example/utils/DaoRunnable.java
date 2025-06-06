package org.example.utils;

import org.example.exception.DaoException;
import org.example.exception.ServiceException;

@FunctionalInterface
public interface DaoRunnable {
    void run() throws DaoException, ServiceException;
}

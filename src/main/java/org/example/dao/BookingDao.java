package org.example.dao;


import org.example.dao.storage.MapStorage;
import org.example.model.entities.Booking;

import java.util.AbstractMap;

public class BookingDao extends AbstractMapDao<Booking> {
    public BookingDao(MapStorage<Booking> storage, Class<Booking> entityClass) {
        super(storage, entityClass);
    }
}
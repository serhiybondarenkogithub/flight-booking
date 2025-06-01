package org.example.dao;


import org.example.dao.storage.ListStorage;
import org.example.model.entities.Booking;

public abstract class BookingDao extends AbstractDao<Booking> {
    public BookingDao(ListStorage<Booking> storage, Class<Booking> entityClass) {
        super(storage);
    }
}
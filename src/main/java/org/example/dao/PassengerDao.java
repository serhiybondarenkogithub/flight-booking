package org.example.dao;

import org.example.model.entities.Passenger;
import org.example.dao.storage.ListStorage;

public abstract class PassengerDao extends AbstractDao<Passenger> {
    public PassengerDao(ListStorage<Passenger> storage, Class<Passenger> entityClass) {
        super(storage, entityClass);
    }
}

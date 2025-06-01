
package org.example.dao;

import org.example.model.entities.Flight;
import org.example.dao.storage.ListStorage;

public abstract class FlightDao extends AbstractDao<Flight> {
    public FlightDao(ListStorage<Flight> storage, Class<Flight> entityClass) {
        super(storage);
    }
}
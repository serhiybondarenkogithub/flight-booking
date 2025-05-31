package org.example.dao;

import org.example.model.Passenger;
import java.util.List;

public interface PassengerDao {
    List<Passenger> findAll();
    void save(Passenger passenger);
}
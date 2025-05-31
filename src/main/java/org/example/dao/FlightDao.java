package org.example.dao;

import org.example.model.Flight;
import java.util.List;
import java.util.Optional;

public interface FlightDao {
    List<Flight> findAll();
    Optional<Flight> findById(String id);
    void saveAll(List<Flight> flights);
    void update(Flight flight);
}
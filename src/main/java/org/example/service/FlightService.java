package org.example.service;

import org.example.model.Flight;
import java.util.List;

public interface FlightService {
    List<Flight> getAllFlights();
    Flight getFlightById(String id);
}
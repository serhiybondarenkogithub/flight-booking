package org.example.service;

import org.example.dao.ListDao;
import org.example.exception.DaoException;
import org.example.model.entities.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class FlightService {
    private ListDao<Flight> dao;

    public FlightService(ListDao<Flight> dao) {
        this.dao = dao;
    }

    public List<Flight> readAll() {
        try {
            return dao.readAll();
        } catch (DaoException e) {
            System.out.println(e);
            return List.of();
        }
    }

    public List<Flight> getFlightsByFlightCode(String code) {
        return readAll().stream().filter(f -> f.flightCode().equals(code)).toList();
    }

    public List<Flight> getFlightsByParams(String originCity, String destinationCity, LocalDate date, int passengers) {
        return readAll().stream().filter(flight -> flight.from().equals(originCity)
                && flight.to().equals(destinationCity)
                && flight.departureDateTime().toLocalDate().equals(date.atStartOfDay().toLocalDate())
                && flight.availableSeats() >= passengers).collect(Collectors.toList());
    }

    public Flight getFlightById(String id) throws IllegalStateException {
        try {
            return dao.read(id).orElseThrow(() -> new IllegalStateException("Flight not found: " + id));
        } catch (DaoException e) {
            System.out.println(e);
            throw new IllegalStateException("Error reading flight", e);
        }
    }

    public void updateFlight(Flight flight) {
        try {
            dao.update(flight);
        } catch (DaoException e) {
            System.out.println(e);
        }
//        return Optional.empty();
    }
}

package org.example.service;

import org.example.dao.FlightDao;
import org.example.exception.DaoException;
import org.example.model.entities.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FlightService {
    private FlightDao dao;
    private List<Flight> flights;

    public FlightService(FlightDao dao) {
        this.dao = dao;
        SaveFlights();
    }

    public void SaveFlights() {
//        FlightsGenerator flightsGenerator = new FlightsGenerator(100);
//        flights = flightsGenerator.getFlights();
        try {
            dao.createAll(flights);
        } catch (DaoException e) {
            System.out.println(e);;
        }
    }

    public List<Flight> readAll() {
        try {
            return dao.readAll();
        } catch (DaoException e) {
            System.out.println(e);
        }
    }

    public List<Flight> getFlightsByParams(String originCity, String destinationCity, LocalDate date, int passengers) {
        return readAll().stream().filter(flight -> flight.from().equals(originCity)
                && flight.to().equals(destinationCity)
                && flight.departureDateTime().toLocalDate().equals(date.atStartOfDay().toLocalDate())
                && flight.availableSeats() >= passengers).collect(Collectors.toList());
    }

    public Flight getFlightById(String id) throws IllegalStateException {
        try {
            return dao.read(id);
        } catch (DaoException e) {
            System.out.println(e);;
        }
    }
}

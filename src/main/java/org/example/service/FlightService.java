package org.example.service;

import org.example.dao.Dao;
import org.example.dao.FlightDao;
import org.example.dao.storage.FileListStorage;
import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.example.utils.FlightsGenerator;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.HashMap;


public class FlightService {
    private Dao<Flight> dao;
    private List<Flight> flights;

    public FlightService(Dao<Flight> dao) {
        this.dao = dao;
    }

    public static void main(String[] args) {
        FileListStorage<Flight> storage = new FileListStorage<>(new File("flights.bin"));
        Dao dao = new FlightDao(storage, Flight.class);
        try {
            storage.saveData(new ArrayList<>());
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
        FlightService service = new FlightService(dao);

        service.init(() -> FlightsGenerator.init(10));
        try {
            List<Flight> data = dao.readAll();
            System.out.println(data);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFlights(List<Flight> flights) {
        try {
            dao.createAll(flights);
        } catch (DaoException e) {
            System.out.println(e);
        }
    }

    public void init(Supplier<List<Flight>> supplier) {
        List<Flight> flights = supplier.get();
        try {
            dao.createAll(flights);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Flight> readAll() {
        try {
            return dao.readAll();
        } catch (DaoException e) {
            System.out.println(e);
            return List.of();
        }
        return null;
    }

    public List<Flight> getFlightsByParams(String originCity, String destinationCity, LocalDate date, int passengers) {
        return readAll().stream().filter(flight -> flight.from().equals(originCity)
                && flight.to().equals(destinationCity)
                && flight.departureDateTime().toLocalDate().equals(date.atStartOfDay().toLocalDate())
                && flight.availableSeats() >= passengers).collect(Collectors.toList());
    }

    public Optional<Flight> getFlightById(String id) throws IllegalStateException {
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

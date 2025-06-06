package org.example.service;

import org.example.dao.ListDao;
import org.example.exception.DaoException;
import org.example.exception.ServiceException;
import org.example.model.entities.Flight;
import org.example.utils.DaoRunnable;
import org.example.utils.DaoSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public class FlightService {
    private final ListDao<Flight> dao;
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    private static final String SERVICE = FlightService.class.getSimpleName();

    public FlightService(ListDao<Flight> dao) {
        this.dao = dao;
    }

    private <T> T supplyDao(DaoSupplier<T> action, String context) throws ServiceException {
        try {
            return action.get();
        } catch (DaoException e) {
            logger.error(SERVICE + ": error during {}", context, e);
            throw new ServiceException(SERVICE + ": " + context + " failed: " + e.getMessage(), e);
        }
    }

    private void runDao(DaoRunnable action, String context) throws ServiceException {
        try {
            action.run();
        } catch (DaoException e) {
            logger.error(SERVICE + ": error during {}", context, e);
            throw new ServiceException(SERVICE + ": " + context + " failed: " + e.getMessage(), e);
        }
    }

    public void initData(Supplier<List<Flight>> flightSupplier) throws ServiceException {
        logger.info("Initializing flights with supplier {}", flightSupplier.getClass().getSimpleName());
        runDao(() -> dao.createAll(flightSupplier.get()), "initializing flights");
        logger.info("Successfully initialized flights");
    }

    public List<Flight> getAllFlights() throws ServiceException {
        return supplyDao(dao::readAll, "reading all flights");
    }

    public List<Flight> findAllByCode(String code) throws ServiceException {
        logger.debug("Searching flights with code={}", code);
        return getAllFlights().stream().filter(f -> f.flightCode().equals(code)).toList();
    }

    public List<Flight> searchFlights(String originCity, String destinationCity, LocalDate date, int passengers) throws ServiceException {
        if (originCity == null || destinationCity == null || date == null || passengers <= 0) {
            throw new IllegalArgumentException("Invalid search parameters");
        }
        logger.info("Searching flights: from={}, to={}, date={}, passengers={}",
                originCity, destinationCity, date, passengers);

        return getAllFlights().stream().filter(flight -> flight.from().equals(originCity)
                && flight.to().equals(destinationCity)
                && flight.departureDateTime().toLocalDate().equals(date)
                && flight.availableSeats() >= passengers).toList();
    }

    public Flight findFlightById(String id) throws ServiceException {
        logger.debug("Fetching flight by id={}", id);
        return supplyDao(
                () -> {
                    Optional<Flight> result = dao.read(id);
                    if (result.isEmpty()) {
                        logger.warn("Flight not found: {}", id);
                        throw new ServiceException("Flight not found: " + id);
                    }
                    return result.get();
                },
                "reading flight by id=" + id
        );
    }

    public void updateFlight(Flight flight) throws ServiceException {
        logger.debug("Updating flight id={}, code={}", flight.getId(), flight.flightCode());
        runDao(() -> dao.update(flight), "updating flight id=" + flight.getId());
    }
}

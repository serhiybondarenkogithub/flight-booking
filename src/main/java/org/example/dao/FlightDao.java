package org.example.dao;

import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.example.dao.storage.ListStorage;

import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Flight> {
    private final ListStorage<Flight> storage;

    public FlightDao(ListStorage<Flight> storage) {
        this.storage = storage;
    }

    private List<Flight> safeLoad() throws DaoException {
        try { return storage.loadData(); }
        catch (StorageException e) { throw new DaoException(e.getMessage(), e); }
    }

    private void safeSave(List<Flight> data) throws DaoException {
        try { storage.saveData(data); }
        catch (StorageException e) { throw new DaoException(e.getMessage(), e); }
    }

    @Override
    public void createAll(List<Flight> flights) throws DaoException {
        if (flights == null || flights.contains(null)) {
            throw new DaoException("Flight list is null or contains null");
        }
        List<Flight> list = safeLoad();
        list.addAll(flights);
        safeSave(safeLoad());
    }

    @Override
    public void create(Flight flight) throws DaoException {
        if (flight == null) throw new DaoException("Flight to create is null");
        List<Flight> list = safeLoad();
        list.add(flight);
        safeSave(list);
    }

    @Override
    public Optional<Flight> read(String id) throws DaoException {
        if (id == null || id.isBlank()) {
            throw new DaoException("Cannot read flight: id is null or blank");
        }

        return safeLoad().stream()
                .filter(obj -> obj.getId().equals(id))
                .findFirst();
    }

    @Override
    public void update(Flight flight) throws DaoException {
        if (flight == null) throw new DaoException("Cannot update null flight");

        List<Flight> flights = safeLoad();
        boolean updated = false;
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getId().equals(flight.getId())) {
                flights.set(i, flight);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new DaoException("Flight with id " + flight.getId() + " not found");
        }
        safeSave(flights);
    }

    @Override
    public void delete(String id) throws DaoException {
        if (id == null) throw new DaoException("Cannot delete flight: id is null");
        List<Flight> flights = safeLoad();
        boolean removed = flights.removeIf(flight -> flight.getId().equals(id));

        if (!removed) {
            throw new DaoException("Flight with id " + id + " not found");
        }
        safeSave(flights);
    }

    @Override
    public List<Flight> readAll() throws DaoException {
        return safeLoad();
    }
}

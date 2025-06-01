package org.example.dao;

import org.example.dao.storage.FileListStorage;
import org.example.dao.storage.ListStorage;
import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightDaoTest {

    @Mock
    private ListStorage<Flight> storage;

    @InjectMocks
    private FlightDao dao;

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = new Flight(
                "FL123", "Kyiv", "Warsaw",
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 12, 0),
                120
        );
    }

    @Test
    void testCreate() throws Exception {
        List<Flight> flights = new ArrayList<>();
        when(storage.loadData()).thenReturn(flights);

        dao.create(flight);

        assertEquals(1, flights.size());
        verify(storage).saveData(flights);
    }

    @Test
    void testReadFlight() throws Exception {
        when(storage.loadData()).thenReturn(List.of(flight));

        Optional<Flight> result = dao.read("FL123");

        assertTrue(result.isPresent());
        assertEquals(flight, result.get());
    }

    @Test
    void testReadNotFound() throws Exception {
        when(storage.loadData()).thenReturn(List.of(flight));

        Optional<Flight> result = dao.read("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreate_throwsStorageException() throws Exception {
        when(storage.loadData()).thenThrow(new StorageException("Load failed"));

        DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));
        System.out.println(ex.getMessage());
        assertTrue(ex.getMessage().equals("FlightDao: Load failed"));
    }

    @Test
    void testCreate_throwsStorageExceptionOnSave() throws Exception {
        List<Flight> flights = new ArrayList<>();
        when(storage.loadData()).thenReturn(flights);
        doThrow(new StorageException("Save failed")).when(storage).saveData(flights);

        DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));

        System.out.println(ex.getMessage());
        assertEquals("FlightDao: Save failed", ex.getMessage());
    }

    @Test
    void testUpdateFlight() throws Exception {
        List<Flight> flights = new ArrayList<>();
        flights.add(flight);
        when(storage.loadData()).thenReturn(flights);

        Flight updatedFlight = flight.withAvailableSeats(100);

        dao.update(updatedFlight);

        assertEquals(100, flights.getFirst().availableSeats());
        verify(storage).saveData(flights);
    }

    @Test
    void testDeleteFlight() throws Exception {
        List<Flight> flights = new ArrayList<>();
        flights.add(flight);
        when(storage.loadData()).thenReturn(flights);

        dao.delete("FL123");

        assertFalse(flights.contains(flight));
        verify(storage).saveData(flights);
    }

    @Test
    void testReadAll() throws Exception {
        List<Flight> flights = List.of(flight);
        when(storage.loadData()).thenReturn(flights);

        List<Flight> result = dao.readAll();

        assertEquals(1, result.size());
        assertEquals(flight, result.getFirst());
    }
}

package org.example.dao;

import org.example.dao.storage.ListStorage;
import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlightDaoTest {

    @Mock
    private ListStorage<Flight> storage;

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
        dao = new FlightDao(storage, Flight.class);
    }

    @DisplayName("Success case")
    @Test
    void testCreateAll_success() throws Exception {
        List<Flight> existingFlights = new ArrayList<>();
        when(storage.loadData()).thenReturn(existingFlights);

        Flight flight1 = new Flight("FL001", "Kyiv", "Berlin",
                LocalDateTime.of(2025, 6, 1, 8, 0),
                LocalDateTime.of(2025, 6, 1, 10, 0),
                100);

        Flight flight2 = new Flight("FL002", "Kyiv", "Paris",
                LocalDateTime.of(2025, 6, 2, 9, 0),
                LocalDateTime.of(2025, 6, 2, 12, 0),
                90);

        List<Flight> newFlights = List.of(flight1, flight2);
        assertNotNull(flight1);
        assertNotNull(flight2);

        dao.createAll(newFlights);

        assertEquals(2, existingFlights.size());
        assertTrue(existingFlights.containsAll(newFlights));
        verify(storage).saveData(existingFlights);
    }

    @DisplayName("Throws when list is null")
    @Test
    void testCreateAll_throwsWhenListIsNull() {
        DaoException ex = assertThrows(DaoException.class, () -> dao.createAll(null));
        assertEquals("Flight list is null", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Throws when list contains null")
    @Test
    void testCreateAll_throwsWhenListContainsNull() {
        List<Flight> listWithNull = Arrays.asList(flight, null);
        DaoException ex = assertThrows(DaoException.class, () -> dao.createAll(listWithNull));
        assertEquals("List contains null at index 1", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Success case")
    @Test
    void testCreate_success() throws Exception {
        List<Flight> flights = new ArrayList<>();
        when(storage.loadData()).thenReturn(flights);

        dao.create(flight);

        assertEquals(1, flights.size());
        verify(storage).saveData(flights);
    }

    @DisplayName("Success creation of empty list")
    @Test
    void testCreateAll_emptyList() throws Exception {
        List<Flight> existingFlights = new ArrayList<>();
        when(storage.loadData()).thenReturn(existingFlights);

        dao.createAll(List.of());

        assertEquals(0, existingFlights.size());
        verify(storage).saveData(existingFlights);
    }

    @DisplayName("Throws when flight is null")
    @Test
    void testCreate_throwsWhenFlightIsNull() {
        DaoException ex = assertThrows(DaoException.class, () -> dao.create(null));
        assertEquals("Flight to create is null", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Throws when storage exception occurs on load")
    @Test
    void testCreate_throwsStorageExceptionOnLoad() throws Exception {
        when(storage.loadData()).thenThrow(new StorageException("Load failed"));

        DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));
        System.out.println(ex.getMessage());
        assertTrue(ex.getMessage().equals("FlightDao: Load failed"));
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Throws when storage exception occurs on save")
    @Test
    void testCreate_throwsStorageExceptionOnSave() throws Exception {
        List<Flight> flights = new ArrayList<>();
        when(storage.loadData()).thenReturn(flights);
        doThrow(new StorageException("Save failed")).when(storage).saveData(flights);

        DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));

        System.out.println(ex.getMessage());
        assertEquals("FlightDao: Save failed", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Success case")
    @Test
    void testRead_success() throws Exception {
        when(storage.loadData()).thenReturn(List.of(flight));

        Optional<Flight> result = dao.read("FL123");

        assertTrue(result.isPresent());
        assertEquals(flight, result.get());
    }

    @DisplayName("Empty optional when id is unknown")
    @Test
    void testRead_notFound() throws Exception {
        when(storage.loadData()).thenReturn(List.of(flight));

        Optional<Flight> result = dao.read("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @DisplayName("Throws when id is null")
    @Test
    void testRead_throwsWhenIdIsNull() {
        DaoException ex = assertThrows(DaoException.class, () -> dao.read(null));
        assertEquals("Cannot read Flight: id is null or blank", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Throws when id is blank")
    @Test
    void testRead_throwsWhenIdIsBlank() {
        DaoException ex1 = assertThrows(DaoException.class, () -> dao.read(""));
        assertEquals("Cannot read Flight: id is null or blank", ex1.getMessage());
        verifyNoMoreInteractions(storage);

        DaoException ex2 = assertThrows(DaoException.class, () -> dao.read(" "));
        assertEquals("Cannot read Flight: id is null or blank", ex2.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Success case")
    @Test
    void testUpdate_success() throws Exception {
        List<Flight> flights = new ArrayList<>();
        flights.add(flight);
        when(storage.loadData()).thenReturn(flights);

        Flight updatedFlight = flight.withAvailableSeats(100);

        dao.update(updatedFlight);

        assertEquals(100, flights.getFirst().availableSeats());
        verify(storage).saveData(flights);
    }

    @DisplayName("Throws when flight is null")
    @Test
    void testUpdate_throwsWhenFlightIsNull() {
        DaoException ex = assertThrows(DaoException.class, () -> dao.update(null));
        assertEquals("Cannot update null Flight", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Throws when flight not found")
    @Test
    void testUpdate_notFound() throws Exception {
        List<Flight> flights = new ArrayList<>();
        when(storage.loadData()).thenReturn(flights);

        Flight unknownFlight = new Flight(
                "UNKNOWN_ID", "Lviv", "Rome",
                LocalDateTime.of(2025, 7, 1, 6, 0),
                LocalDateTime.of(2025, 7, 1, 9, 0),
                50
        );

        DaoException ex = assertThrows(DaoException.class, () -> dao.update(unknownFlight));

        assertEquals("Flight with id UNKNOWN_ID not found", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Update should replace all fields except id")
    @Test
    void testUpdate_replacesAllFieldsExceptId() throws Exception {
        Flight original = new Flight("FL123", "Kyiv", "Warsaw",
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 12, 0), 120);

        List<Flight> flights = new ArrayList<>(List.of(original));
        when(storage.loadData()).thenReturn(flights);

        Flight updated = new Flight("FL123", "Lviv", "London",
                LocalDateTime.of(2025, 6, 5, 8, 0),
                LocalDateTime.of(2025, 6, 5, 11, 0), 95);

        dao.update(updated);

        assertEquals("Lviv", flights.getFirst().from());
        assertEquals("London", flights.getFirst().to());
        assertEquals(95, flights.getFirst().availableSeats());
        verify(storage).saveData(flights);
    }

    @DisplayName("Success case")
    @Test
    void testDelete_success() throws Exception {
        List<Flight> flights = new ArrayList<>();
        flights.add(flight);
        when(storage.loadData()).thenReturn(flights);

        dao.delete("FL123");

        assertFalse(flights.contains(flight));
        verify(storage).saveData(flights);
    }

    @DisplayName("Throws when flight not found")
    @Test
    void testDelete_notFound() throws Exception {
        List<Flight> flights = new ArrayList<>();
        when(storage.loadData()).thenReturn(flights);

        DaoException ex = assertThrows(DaoException.class, () -> dao.delete("UNKNOWN_ID"));

        assertEquals("Flight with id UNKNOWN_ID not found", ex.getMessage());
        verify(storage, never()).saveData(any());
        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Throws when id is null or blank")
    @Test
    void testDelete_throwsWhenIdIsNullOrBlank() {
        DaoException ex1 = assertThrows(DaoException.class, () -> dao.delete(null));
        assertEquals("Cannot delete Flight: id is null or blank", ex1.getMessage());

        DaoException ex2 = assertThrows(DaoException.class, () -> dao.delete(""));
        assertEquals("Cannot delete Flight: id is null or blank", ex2.getMessage());

        DaoException ex3 = assertThrows(DaoException.class, () -> dao.delete(" "));
        assertEquals("Cannot delete Flight: id is null or blank", ex3.getMessage());

        verifyNoMoreInteractions(storage);
    }

    @DisplayName("Success case")
    @Test
    void testReadAll_success() throws Exception {
        List<Flight> flights = List.of(flight);
        when(storage.loadData()).thenReturn(flights);

        List<Flight> result = dao.readAll();

        assertEquals(1, result.size());
        assertEquals(flight, result.getFirst());
    }

    @DisplayName("Throws when storage exception occurs on load")
    @Test
    void testReadAll_throwsStorageException() throws Exception {
        when(storage.loadData()).thenThrow(new StorageException("Load failed"));

        DaoException ex = assertThrows(DaoException.class, () -> dao.readAll());
        assertEquals("FlightDao: Load failed", ex.getMessage());
        verifyNoMoreInteractions(storage);
    }
}

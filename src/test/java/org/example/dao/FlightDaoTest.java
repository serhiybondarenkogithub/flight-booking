package org.example.dao;

import org.example.dao.storage.ListStorage;
import org.example.exception.DaoException;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Nested
    @DisplayName("Create All tests")
    class CreateAllTests {
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
            verify(storage).saveData(argThat(list ->
                    list.size() == 2 && list.stream().anyMatch(f -> f.getId().equals("FL002"))
            ));
        }

        @DisplayName("Allows adding flights with duplicate IDs")
        @Test
        void testCreateAll_allowsDuplicates() throws Exception {
            List<Flight> existingFlights = new ArrayList<>();
            when(storage.loadData()).thenReturn(existingFlights);

            Flight flightDuplicate = new Flight("FL123", "Odessa", "London",
                    LocalDateTime.of(2025, 6, 1, 10, 0),
                    LocalDateTime.of(2025, 6, 1, 12, 0),
                    120);

            dao.createAll(List.of(flight, flightDuplicate));

            assertEquals(2, existingFlights.size());
            assertTrue(existingFlights.containsAll(List.of(flight, flightDuplicate)));
        }

        @Test
        @DisplayName("Handles large number of flights")
        void testCreateAll_manyFlights() throws Exception {
            List<Flight> existingFlights = new ArrayList<>();
            when(storage.loadData()).thenReturn(existingFlights);

            List<Flight> bulkFlights = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                bulkFlights.add(new Flight("FL" + i, "A", "B",
                        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 100));
            }

            dao.createAll(bulkFlights);

            assertEquals(10_000, existingFlights.size());
            verify(storage).saveData(any());
        }

        @Test
        @DisplayName("Throws when storage returns null in createAll")
        void testCreateAll_throwsWhenLoadReturnsNull() throws Exception {
            when(storage.loadData()).thenReturn(null);
            List<Flight> flights = List.of(flight);

            DaoException ex = assertThrows(DaoException.class, () -> dao.createAll(flights));

            assertEquals("FlightDao: null data returned from storage", ex.getMessage());
            verify(storage).loadData();
        }

        @Test
        @DisplayName("Throws when storage exception occurs on load in createAll")
        void testCreateAll_throwsStorageExceptionOnLoad() throws Exception {
            List<Flight> flights = List.of(flight);
            when(storage.loadData()).thenThrow(new StorageException("Load failed"));

            DaoException ex = assertThrows(DaoException.class, () -> dao.createAll(flights));

            assertEquals("FlightDao: Load failed", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on save in createAll")
        void testCreateAll_throwsStorageExceptionOnSave() throws Exception {
            List<Flight> flights = new ArrayList<>();
            when(storage.loadData()).thenReturn(flights);
            doThrow(new StorageException("Save failed")).when(storage).saveData(flights);

            DaoException ex = assertThrows(DaoException.class, () -> dao.createAll(List.of(flight)));

            assertEquals("FlightDao: Save failed", ex.getMessage());
            verify(storage).loadData();
            verify(storage).saveData(flights);
            verifyNoMoreInteractions(storage);
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
    }

    @Nested
    @DisplayName("Create tests")
    class CreateTests {
        @DisplayName("Success case")
        @Test
        void testCreate_success() throws Exception {
            List<Flight> flights = new ArrayList<>();
            when(storage.loadData()).thenReturn(flights);

            dao.create(flight);

            assertEquals(1, flights.size());
            verify(storage).saveData(argThat(list ->
                    list.size() == 1 && list.get(0).getId().equals("FL123")
            ));
        }

        @Test
        @DisplayName("Throws when storage returns null in create")
        void testCreate_throwsWhenLoadReturnsNull() throws Exception {
            when(storage.loadData()).thenReturn(null);

            DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));

            assertEquals("FlightDao: null data returned from storage", ex.getMessage());
            verify(storage).loadData();
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

        @ParameterizedTest
        @NullSource
        @DisplayName("Throws when flight is null")
        void testCreate_throwsWhenFlightIsNull(Flight flight) {
            DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));
            assertEquals("Flight to create is null", ex.getMessage());
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on load")
        void testCreate_throwsStorageExceptionOnLoad() throws Exception {
            when(storage.loadData()).thenThrow(new StorageException("Load failed"));

            DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));
            assertTrue(ex.getMessage().equals("FlightDao: Load failed"));
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on save")
        void testCreate_throwsStorageExceptionOnSave() throws Exception {
            List<Flight> flights = new ArrayList<>();
            when(storage.loadData()).thenReturn(flights);
            doThrow(new StorageException("Save failed")).when(storage).saveData(flights);

            DaoException ex = assertThrows(DaoException.class, () -> dao.create(flight));

            assertEquals("FlightDao: Save failed", ex.getMessage());
            verify(storage).loadData(); // додано
            verify(storage).saveData(flights);
            verifyNoMoreInteractions(storage);
        }
    }

    @Nested
    @DisplayName("Read tests")
    class ReadTests {
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

        @Test
        @DisplayName("Throws when storage returns null in read")
        void testRead_throwsWhenLoadReturnsNull() throws Exception {
            when(storage.loadData()).thenReturn(null);

            DaoException ex = assertThrows(DaoException.class, () -> dao.read("FL123"));

            assertEquals("FlightDao: null data returned from storage", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on read")
        void testRead_throwsStorageExceptionOnLoad() throws Exception {
            when(storage.loadData()).thenThrow(new StorageException("Load failed"));

            DaoException ex = assertThrows(DaoException.class, () -> dao.read("FL123"));

            assertEquals("FlightDao: Load failed", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = { "", " ", "\t", "\n" })
        @DisplayName("Throws when id is null or blank")
        void testRead_throwsWhenIdIsNullOrBlank(String id) {
            DaoException ex = assertThrows(DaoException.class, () -> dao.read(id));
            assertEquals("Cannot read Flight: id is null or blank", ex.getMessage());
            verifyNoMoreInteractions(storage);
        }
    }

    @Nested
    @DisplayName("Update tests")
    class UpdateTests {
        @Test
        @DisplayName("Success case")
        void testUpdate_success() throws Exception {
            List<Flight> flights = new ArrayList<>();
            flights.add(flight);
            when(storage.loadData()).thenReturn(flights);

            Flight updatedFlight = flight.withAvailableSeats(100);

            dao.update(updatedFlight);

            assertEquals(100, flights.getFirst().availableSeats());
            verify(storage).saveData(argThat(list ->
                    list.size() == 1 &&
                            list.get(0).getId().equals("FL123") &&
                            list.get(0).availableSeats() == 100
            ));
        }

        @Test
        @DisplayName("Throws when storage returns null in update")
        void testUpdate_throwsWhenLoadReturnsNull() throws Exception {
            when(storage.loadData()).thenReturn(null);

            DaoException ex = assertThrows(DaoException.class, () -> dao.update(flight));

            assertEquals("FlightDao: null data returned from storage", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on save in update")
        void testUpdate_throwsStorageExceptionOnSave() throws Exception {
            List<Flight> flights = new ArrayList<>(List.of(flight));
            when(storage.loadData()).thenReturn(flights);
            doThrow(new StorageException("Save failed")).when(storage).saveData(flights);

            DaoException ex = assertThrows(DaoException.class, () -> dao.update(flight));

            assertEquals("FlightDao: Save failed", ex.getMessage());
            verify(storage).loadData(); // додано
            verify(storage).saveData(flights);
            verifyNoMoreInteractions(storage);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Throws when flight is null")
        void testUpdate_throwsWhenFlightIsNull(Flight flight) {
            DaoException ex = assertThrows(DaoException.class, () -> dao.update(flight));
            assertEquals("Cannot update null Flight", ex.getMessage());
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when flight not found")
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
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Update should replace all fields except id")
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

            assertAll(
                    () -> assertEquals("Lviv", flights.getFirst().from()),
                    () -> assertEquals("London", flights.getFirst().to()),
                    () -> assertEquals(95, flights.getFirst().availableSeats())
            );

            verify(storage).saveData(argThat(list ->
                    list.size() == 1 &&
                            list.get(0).getId().equals("FL123") &&
                            list.get(0).from().equals("Lviv") &&
                            list.get(0).to().equals("London") &&
                            list.get(0).availableSeats() == 95
            ));
        }

    }

    @Nested
    @DisplayName("Delete tests")
    class DeleteTests {
        @DisplayName("Success case")
        @Test
        void testDelete_success() throws Exception {
            List<Flight> flights = new ArrayList<>();
            flights.add(flight);
            when(storage.loadData()).thenReturn(flights);

            dao.delete("FL123");

            assertFalse(flights.contains(flight));
            verify(storage).saveData(argThat(list ->
                    list.stream().noneMatch(f -> f.getId().equals("FL123"))
            ));
        }

        @Test
        @DisplayName("Throws when flight not found")
        void testDelete_notFound() throws Exception {
            List<Flight> flights = new ArrayList<>();
            when(storage.loadData()).thenReturn(flights);

            DaoException ex = assertThrows(DaoException.class, () -> dao.delete("UNKNOWN_ID"));

            assertEquals("Flight with id UNKNOWN_ID not found", ex.getMessage());
            verify(storage).loadData();
            verify(storage, never()).saveData(any());
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage returns null in delete()")
        void testDelete_throwsWhenLoadReturnsNull() throws Exception {
            when(storage.loadData()).thenReturn(null);

            DaoException ex = assertThrows(DaoException.class, () -> dao.delete("FL123"));
            assertEquals("FlightDao: null data returned from storage", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "\t", "\n" })
        @NullSource
        @DisplayName("Throws when id is null or blank")
        void testDelete_throwsWhenIdIsNullOrBlank(String id) {
            DaoException ex1 = assertThrows(DaoException.class, () -> dao.delete(id));
            assertEquals("Cannot delete Flight: id is null or blank", ex1.getMessage());
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on save in delete")
        void testDelete_throwsStorageExceptionOnSave() throws Exception {
            List<Flight> flights = new ArrayList<>(List.of(flight));
            when(storage.loadData()).thenReturn(flights);
            doThrow(new StorageException("Save failed")).when(storage).saveData(flights);

            DaoException ex = assertThrows(DaoException.class, () -> dao.delete("FL123"));

            assertEquals("FlightDao: Save failed", ex.getMessage());
            verify(storage).loadData(); // додано
            verify(storage).saveData(flights);
            verifyNoMoreInteractions(storage);
        }

    }

    @Nested
    @DisplayName("Read All tests")
    class ReadAllTests {
        @Test
        @DisplayName("Success case")
        void testReadAll_success() throws Exception {
            List<Flight> flights = List.of(flight);
            when(storage.loadData()).thenReturn(flights);

            List<Flight> result = dao.readAll();

            assertEquals(1, result.size());
            assertEquals(flight, result.getFirst());
        }

        @Test
        @DisplayName("Returned list from readAll is unmodifiable")
        void testReadAll_returnsUnmodifiableList() throws Exception {
            List<Flight> originalList = new ArrayList<>(List.of(flight));
            when(storage.loadData()).thenReturn(originalList);

            List<Flight> result = dao.readAll();

            assertEquals(1, result.size());
            assertEquals(flight, result.get(0));

            assertThrows(UnsupportedOperationException.class, () -> result.add(
                    new Flight("FL999", "Lviv", "Rome", LocalDateTime.now(), LocalDateTime.now().plusHours(2), 100)
            ));
        }

        @Test
        @DisplayName("Returns list with multiple flights")
        void testReadAll_multipleFlights() throws Exception {
            List<Flight> flights = List.of(flight, new Flight("FL999", "Lviv", "Berlin",
                    LocalDateTime.now(), LocalDateTime.now().plusHours(2), 80));
            when(storage.loadData()).thenReturn(flights);

            List<Flight> result = dao.readAll();

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(f -> f.getId().equals("FL999")));
        }

        @Test
        @DisplayName("ReadAll handles large dataset")
        void testReadAll_largeDataset() throws Exception {
            List<Flight> bigList = new ArrayList<>();
            for (int i = 0; i < 100_000; i++) {
                bigList.add(new Flight("FL" + i, "A", "B", LocalDateTime.now(), LocalDateTime.now().plusHours(1), 100));
            }
            when(storage.loadData()).thenReturn(bigList);

            List<Flight> result = dao.readAll();
            assertEquals(100_000, result.size());
        }

        @Test
        @DisplayName("Returns empty list when storage is empty")
        void testReadAll_emptyList() throws Exception {
            when(storage.loadData()).thenReturn(List.of());

            List<Flight> result = dao.readAll();

            assertTrue(result.isEmpty());
            verify(storage).loadData();
        }

        @Test
        @DisplayName("Throws when storage returns null instead of list in readAll")
        void testReadAll_throwsWhenLoadReturnsNull() throws Exception {
            when(storage.loadData()).thenReturn(null);

            DaoException ex = assertThrows(DaoException.class, () -> dao.readAll());
            assertEquals("FlightDao: null data returned from storage", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }

        @Test
        @DisplayName("Throws when storage exception occurs on load in readAll")
        void testReadAll_throwsStorageExceptionOnLoad() throws Exception {
            when(storage.loadData()).thenThrow(new StorageException("Load failed"));

            DaoException ex = assertThrows(DaoException.class, () -> dao.readAll());

            assertEquals("FlightDao: Load failed", ex.getMessage());
            verify(storage).loadData();
            verifyNoMoreInteractions(storage);
        }
    }
}

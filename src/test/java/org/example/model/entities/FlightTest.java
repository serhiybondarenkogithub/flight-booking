package org.example.model.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FlightTest {
    private final LocalDateTime depTime = LocalDateTime.of(2025, 6, 1, 10, 0);
    private final LocalDateTime arrTime = LocalDateTime.of(2025, 6, 1, 14, 0);

    @Test
    void shouldCreateFlightSuccessfully() {
        Flight flight = new Flight("FL123", "Kyiv", "Berlin", depTime, arrTime, 100);

        assertEquals("FL123", flight.id());
        assertEquals("Kyiv", flight.departure());
        assertEquals("Berlin", flight.destination());
        assertEquals(depTime, flight.departureTime());
        assertEquals(arrTime, flight.destinationTime());
        assertEquals(100, flight.availableSeats());
    }

    @Test
    void shouldThrowExceptionForNegativeSeats() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Flight("FL123", "Kyiv", "Berlin", depTime, arrTime, -5)
        );
        assertEquals("Available seats cannot be negative", exception.getMessage());
    }


    @Test
    void shouldThrowExceptionForNullFields() {
        assertThrows(IllegalArgumentException.class, () ->
                new Flight(null, "Kyiv", "Berlin", depTime, arrTime, 50));
        assertThrows(IllegalArgumentException.class, () ->
                new Flight("FL123", null, "Berlin", depTime, arrTime, 50));
        assertThrows(IllegalArgumentException.class, () ->
                new Flight("FL123", "Kyiv", null, depTime, arrTime, 50));
        assertThrows(IllegalArgumentException.class, () ->
                new Flight("FL123", "Kyiv", "Berlin", null, arrTime, 50));
        assertThrows(IllegalArgumentException.class, () ->
                new Flight("FL123", "Kyiv", "Berlin", depTime, null, 50));
    }

    @Test
    void withAvailableSeatsShouldReturnNewInstance() {
        Flight original = new Flight("FL123", "Kyiv", "Berlin", depTime, arrTime, 100);
        Flight modified = original.withAvailableSeats(50);

        assertNotSame(original, modified);
        assertEquals(50, modified.availableSeats());
        assertEquals(100, original.availableSeats()); // original is unchanged
    }

    @Test
    void withAvailableSeatsShouldThrowExceptionForNegative() {
        Flight flight = new Flight("FL123", "Kyiv", "Berlin", depTime, arrTime, 100);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                flight.withAvailableSeats(-1)
        );
        assertEquals("Available seats cannot be negative", exception.getMessage());
    }
}

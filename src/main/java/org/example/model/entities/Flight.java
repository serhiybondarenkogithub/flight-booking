package org.example.model.entities;

import org.example.model.interfaces.Identifiable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public record Flight(
        String id,
        String departure,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime destinationTime,
        int availableSeats
) implements Identifiable, Serializable {

    public Flight {
        if (id == null || departure == null || destination == null || departureTime == null || destinationTime == null)
            throw new IllegalArgumentException("Flight parameters cannot be null");

        validateSeats(availableSeats);
    }

    public Flight withAvailableSeats(int newSeats) {
        validateSeats(newSeats);
        return new Flight(id, departure, destination, departureTime, destinationTime, newSeats);
    }

    private void validateSeats(int seats) {
        if (seats < 0) {
            throw new IllegalArgumentException("Available seats cannot be negative");
        }
    }

    @Override
    public String getId() {
        return id;
    }
}
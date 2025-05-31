package org.example.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Flight {
    private String id;
    private String destination;
    private LocalDateTime departureTime;
    private int availableSeats;

    public Flight(String id, String destination, LocalDateTime departureTime, int availableSeats) {
        this.id = id;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
    }

    public String getId() { return id; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight)) return false;
        Flight flight = (Flight) o;
        return Objects.equals(id, flight.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
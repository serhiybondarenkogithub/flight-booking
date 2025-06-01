package org.example.model.entities;

import org.example.model.interfaces.Identifiable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Flight implements Identifiable, Serializable {
    private String id;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime destinationTime;
    private int availableSeats;
    private String departure;

    public Flight(String id, String departure, String destination, LocalDateTime departureTime LocalDateTime destinationTime, int availableSeats) {
        if (id == null || departure == null || destination == null || departureTime == null || destinationTime == null)
            throw new IllegalArgumentException("Flight parameters cannot be null");

        this.id = id;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.availableSeats = availableSeats;
    }

    public String getId() { return id; }
    public String getDeparture() { return departure; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getDestinationTime() { return destinationTime; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) {
        if (availableSeats < 0) {
            throw new IllegalArgumentException("Avaliable seats cannot be negative");
        }
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id='" + id + '\'' +
                ", departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                ", destinationTime=" + destinationTime +
                ", availableSeats=" + availableSeats +
                '}';
    }

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
package org.example.model.entities;

import org.example.model.interfaces.Identifiable;

import java.util.List;
import java.util.Objects;

public class Booking implements Identifiable {
    private String id;
    private String flightId;
    private List<Passenger> passengers;

    public Booking(String id, String flightId, List<Passenger> passengers) {
        this.id = id;
        this.flightId = flightId;
        this.passengers = passengers;
    }

    public String getId() {
        return id;
    }

    public String getFlightId() {
        return flightId;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
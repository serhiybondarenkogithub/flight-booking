package org.example.service;

import org.example.model.entities.Booking;
import org.example.model.entities.Passenger;
import java.util.List;

public interface BookingService {
    Booking bookFlight(String flightId, List<Passenger> passengers);
    boolean cancelBooking(String bookingId);
    List<Booking> getBookingsByPassenger(Passenger passenger);
}
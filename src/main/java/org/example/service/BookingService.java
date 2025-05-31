package org.example.service;

import org.example.model.Booking;
import org.example.model.Passenger;
import java.util.List;

public interface BookingService {
    Booking bookFlight(String flightId, List<Passenger> passengers);
    boolean cancelBooking(String bookingId);
    List<Booking> getBookingsByPassenger(Passenger passenger);
}
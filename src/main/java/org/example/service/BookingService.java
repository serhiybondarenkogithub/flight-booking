package org.example.service;

import org.example.model.entities.Booking;
import org.example.model.entities.Flight;
import org.example.model.entities.Passenger;


import java.util.*;

public class BookingService {
    private final Map<String, Booking> bookings = new HashMap<>();
    private final FlightService flightService;

    public BookingService(FlightService flightService) {
        this.flightService = flightService;
    }

    public void bookSeat(Flight flight, List<Passenger> passengers) {
        if (flight.availableSeats() < passengers.size()) {
            System.out.println("Not enough available seats on flight " + flight.id());
            return;
        }
        Flight updatedFlight = flight.withAvailableSeats(flight.availableSeats() - passengers.size());
        flightService.updateFlight(updatedFlight);
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, flight.id(), passengers);
        bookings.put(bookingId, booking);
        System.out.println("Booked " + passengers.size() + " seat(s) on flight " + flight.id());
    }

    public void cancelBookingByFlightAndPassenger(String flightId, String passengerId) {
        Booking foundBooking = null;
        Passenger foundPassenger = null;

        for (Booking booking : bookings.values()) {
            if (booking.getFlightId().equals(flightId)) {
                for (Passenger passenger : booking.getPassengers()) {
                    if (passenger.getId().equals(passengerId)) {
                        foundBooking = booking;
                        foundPassenger = passenger;
                        break;
                    }
                }
            }
            if (foundBooking != null) break;
        }

        if (foundBooking == null || foundPassenger == null) {
            System.out.println("No booking found for flight " + flightId + " and passenger " + passengerId);
            return;
        }

        foundBooking.getPassengers().remove(foundPassenger);

        Flight flight = flightService.getFlightById(flightId);
        if (flight != null) {
            Flight updatedFlight = flight.withAvailableSeats(flight.availableSeats() + 1);
            flightService.updateFlight(updatedFlight);
        }

        if (foundBooking.getPassengers().isEmpty()) {
            bookings.remove(foundBooking.getId());
            System.out.println("Booking " + foundBooking.getId() + " cancelled (all passengers removed).");
        } else {
            if (flight != null) {
                System.out.println("Passenger " + passengerId + " removed from booking. Flight details: " +
                        "Flight ID: " + flight.id() +
                        ", From: " + flight.from() +
                        ", To: " + flight.to() +
                        ", Date: " + flight.getDepartureTime().toLocalDate());
            } else {
                System.out.println("Passenger " + passengerId + " removed from booking.");
            }
        }
    }

    public void showBookingsForUser(String userId) {
        boolean found = false;
        for (Booking booking : bookings.values()) {
            for (Passenger passenger : booking.getPassengers()) {
                if (passenger.getId().equals(userId)) {
                    Flight flight = flightService.getFlightById(booking.getFlightId());
                    System.out.println("Flight ID: " + flight.id() +
                            ", From: " + flight.from() +
                            ", To: " + flight.to() +
                            ", Date: " + flight.getDepartureTime().toLocalDate());
                    System.out.print("Passengers: ");
                    for (Passenger p : booking.getPassengers()) {
                        System.out.print(p.getFirstName() + " " + p.getLastName() + "; ");
                    }
                    System.out.println();
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No bookings found for user: " + userId);
        }
    }
}
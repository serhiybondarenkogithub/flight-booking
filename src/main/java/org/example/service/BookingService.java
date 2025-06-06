package org.example.service;

import org.example.dao.MapDao;
import org.example.exception.DaoException;
import org.example.exception.ServiceException;
import org.example.model.entities.Booking;
import org.example.model.entities.Flight;
import org.example.model.entities.Passenger;


import java.util.*;

public class BookingService {
    private final FlightService flightService;
    private final MapDao<Booking> dao;

    public BookingService(FlightService flightService, MapDao<Booking> dao) {

        this.flightService = flightService;
        this.dao = dao;
    }

    public void bookSeat(Flight flight, List<Passenger> passengers) {
        if (flight.availableSeats() < passengers.size()) {
            System.out.println("Недостатньо місць для бронювання рейсом " + flight.flightCode());
            return;
        }
        Flight updatedFlight = flight.withAvailableSeats(flight.availableSeats() - passengers.size());
        try {
            flightService.updateFlight(updatedFlight);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, flight.id(), passengers);
        try {
            dao.create(bookingId, booking);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Заброньовано місць: " + passengers.size() + ". Номер рейсу: " + flight.flightCode());
    }

    public void cancelBookingByFlightAndPassenger(String flightId, String passengerId) {
        Booking foundBooking = null;
        Passenger foundPassenger = null;

        try {
            for (Booking booking : dao.readAll().values()) {
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
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        if (foundBooking == null || foundPassenger == null) {
            System.out.println("No booking found for flight " + flightId + " and passenger " + passengerId);
            return;
        }

        foundBooking.getPassengers().remove(foundPassenger);

        Flight flight = null;
        try {
            flight = flightService.findFlightById(flightId);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        if (flight != null) {
            Flight updatedFlight = flight.withAvailableSeats(flight.availableSeats() + 1);
            try {
                flightService.updateFlight(updatedFlight);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }

        if (foundBooking.getPassengers().isEmpty()) {
            try {
                dao.delete(foundBooking.getId());
            } catch (DaoException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Бронювання " + foundBooking.getId() + " відмінено (пасажири видалені).");
        } else {
            if (flight != null) {
                System.out.println("Пасажир " + passengerId + " відмінив бронювання. Деталі польоту: " +
                        "Номер рейсу: " + flight.flightCode() +
                        ", з: " + flight.from() +
                        ", до: " + flight.to() +
                        ", дата: " + flight.getDepartureTime().toLocalDate());
            } else {
                System.out.println("Пасажир " + passengerId + " видалений з броювання.");
            }
        }
    }

    public void showBookingsForUser(String userId) {
        boolean found = false;
        try {
            for (Booking booking : dao.readAll().values()) {
                for (Passenger passenger : booking.getPassengers()) {
                    if (passenger.getId().equals(userId)) {
                        Flight flight = null;
                        try {
                            flight = flightService.findFlightById(booking.getFlightId());
                        } catch (ServiceException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Номер рейсу: " + flight.flightCode() +
                                ", з: " + flight.from() +
                                ", до: " + flight.to() +
                                ", дата: " + flight.getDepartureTime().toLocalDate());
                        System.out.print("Пасажири: ");
                        for (Passenger p : booking.getPassengers()) {
                            System.out.print(p.getFirstName() + " " + p.getLastName() + "; ");
                        }
                        System.out.println();
                        found = true;
                    }
                }
            }
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
        if (!found) {
            System.out.println("Немає бронюваня для пасажира: " + userId);
        }
    }
}
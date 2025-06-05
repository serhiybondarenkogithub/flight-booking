package org.example.controller;

import org.example.model.entities.Flight;
import org.example.service.FlightService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class FlightController {

    private final FlightService flightService;
    private final Scanner scanner = new Scanner(System.in);

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    public void showBoard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24h = now.plusHours(24);
        List<Flight> flights = flightService.readAll().stream()
                .filter(f -> "Kyiv".equalsIgnoreCase(f.from())
                        && !f.departureDateTime().isBefore(now)
                        && !f.departureDateTime().isAfter(next24h))
                .toList();
        if (flights.isEmpty()) {
            System.out.println("No flights from Kyiv in the next 24 hours.");
        } else {
            flights.forEach(System.out::println);
        }
        System.out.println("Returning to main menu...");
    }

    public void showFlightInfo(String flightId) {
        try {
            Flight flight = flightService.getFlightById(flightId);
            System.out.println("Flight ID: " + flight.id());
            System.out.println("From: " + flight.from());
            System.out.println("To: " + flight.to());
            System.out.println("Date: " + flight.departureDateTime().toLocalDate());
            System.out.println("Time: " + flight.departureDateTime().toLocalTime());
            System.out.println("Available seats: " + flight.availableSeats());
        } catch (IllegalStateException e) {
            System.out.println("Flight not found: " + flightId);
        }
        System.out.println("Returning to main menu...");
    }


}
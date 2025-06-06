package org.example.controller;

import org.example.console.Console;
import org.example.model.entities.Flight;
import org.example.service.FlightService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    private List<Flight> getFutureFlights() {
        return flightService.readAll().stream()
                .filter(f -> "Kyiv".equalsIgnoreCase(f.from())
                        && !f.departureDateTime().isBefore(LocalDateTime.now())
                        && !f.departureDateTime().isAfter(LocalDateTime.now().plusHours(24)))
                .toList();
    }

    public void showBoard() {
        List<Flight> flights = getFutureFlights();
        if (flights.isEmpty()) {
            System.out.println("Відсутні польоти найближчі 24 години");
        } else {
            printFlightBoard(flights);
        }
    }

    public void showFlightInfo(String flightCode, Scanner scanner) {
        List<Flight> flights = getFutureFlights();
        if (flights.isEmpty()) {
            System.out.println("Відсутні польоти найближчі 24 години");
        } else {
            List<Flight> search = flights.stream().filter(f -> f.flightCode().equals(flightCode)).toList();
            if (search.size() == 1) {
                printFlightDetails(search.getFirst());
            } else if (search.size() > 1) {
                System.out.println("Знайдено рейсів: " + search.size());
                printFligsWithIndexes(search);
                System.out.println("Оберіть за індексом: ");
                String input = scanner.nextLine();
                try {
                    int index = Integer.parseInt(input);

                    if (index > 0 && index <= search.size()) {
                        Flight flight = search.get(index - 1);
                        printFlightDetails(flight);
                    } else {
                        System.out.println("❌ Некоректний індекс. Має бути від 1 до " + (search.size()));
                    }

                } catch (NumberFormatException e) {
                    System.out.println("❌ Ви ввели не число.");
                }
            } else {
                System.out.println("Рейс не знайдено");
            }
        }
        System.out.println("Returning to main menu...");
    }

    public static void printFlightBoard(List<Flight> flights) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Заголовок
        System.out.printf("%-10s %-20s %-10s %-10s%n",
                "FLIGHT", "ROUTE", "DEPART", "ARRIVE");

        System.out.println("=".repeat(55));

        // Вивід рейсів
        for (Flight flight : flights) {
            System.out.printf("%-10s %-20s %-10s %-10s%n",
                    flight.flightCode(),
                    flight.from() + " → " + flight.to(),
                    timeFormatter.format(flight.departureDateTime()),
                    timeFormatter.format(flight.arrivalDateTime()));
        }
    }

    public static void printFligsWithIndexes(List<Flight> flights) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Заголовок
        System.out.printf("%-4s %-10s %-20s %-17s %-17s %-5s%n",
                "#", "FLIGHT", "ROUTE", "DEPARTURE", "ARRIVAL", "SEATS");

        System.out.println("=".repeat(80));

        // Вивід кожного рейсу з індексом
        int index = 1;
        for (Flight flight : flights) {
            System.out.printf("%-4d %-10s %-20s %-17s %-17s %-5d%n",
                    index++,
                    flight.flightCode(),
                    flight.from() + " → " + flight.to(),
                    dtf.format(flight.departureDateTime()),
                    dtf.format(flight.arrivalDateTime()),
                    flight.availableSeats());
        }
    }

    public static void printFlightDetails(Flight flight) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.println("📋 Інформація про рейс");
        System.out.println("==========================");
        System.out.println("ID:             " + flight.id());
        System.out.println("Flight code:    " + flight.flightCode());
        System.out.println("From:           " + flight.from());
        System.out.println("To:             " + flight.to());
        System.out.println("Departure:      " + dateTimeFormatter.format(flight.departureDateTime()));
        System.out.println("Arrival:        " + dateTimeFormatter.format(flight.arrivalDateTime()));
        System.out.println("Available seats:" + flight.availableSeats());
        System.out.println("==========================");
    }
}
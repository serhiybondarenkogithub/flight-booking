package org.example.controller;

import org.example.model.entities.Flight;
import org.example.model.entities.Passenger;
import org.example.service.BookingService;
import org.example.service.FlightService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookingController {

    private final FlightService flightService;
    private final BookingService bookingService;
    private final Scanner scanner = new Scanner(System.in);

    public BookingController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    public void cancelBooking(Scanner scanner) {
        System.out.print("Enter flight ID: ");
        String flightId = scanner.nextLine();
        System.out.print("Enter passenger ID: ");
        String passengerId = scanner.nextLine();
        bookingService.cancelBookingByFlightAndPassenger(flightId, passengerId);
    }

    public void showMyBookings(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine();
        bookingService.showBookingsForUser(userId);
    }

    public void searchAndBook() {
        System.out.print("Enter departure city: ");
        String from = scanner.nextLine();
        System.out.print("Enter destination: ");
        String to = scanner.nextLine();
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter number of passengers: ");
        int passengerCount = Integer.parseInt(scanner.nextLine());

        List<Flight> found = flightService.getFlightsByParams(from, to, java.time.LocalDate.parse(date), passengerCount);
        if (found.isEmpty()) {
            System.out.println("No flights found.");
            System.out.println("Returning to main menu...");
            return;
        }
        for (int i = 0; i < found.size(); i++) {
            System.out.println((i + 1) + ". " + found.get(i));
        }
        System.out.println("0. Return to main menu");
        System.out.print("Choose a flight to book (number): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice == 0) {
            System.out.println("Returning to main menu...");
            return;
        }
        if (choice < 1 || choice > found.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        Flight selected = found.get(choice - 1);
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 1; i <= passengerCount; i++) {
            System.out.print("Enter passenger " + i + " first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter passenger " + i + " last name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter passenger id: ");
            String passengerId = scanner.nextLine();
            passengers.add(new Passenger(passengerId, firstName, lastName));
        }
        bookingService.bookSeat(selected, passengers);
        System.out.println("Booking complete. Returning to main menu...");
    }
}
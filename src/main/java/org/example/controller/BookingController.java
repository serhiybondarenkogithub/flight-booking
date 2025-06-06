package org.example.controller;

import org.example.exception.ServiceException;
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
        System.out.print("Введіть код рейсу: ");
        String flightCode = scanner.nextLine();
        List<Flight> found = null;
        try {
            found = flightService.findAllByCode(flightCode);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        if (found.isEmpty()) {
            System.out.println("Рейс не знайдено.");
            System.out.println("Повертаємось до головного меню...");
            return;
        }
        FlightController.printFlightsWithIndexes(found);
        System.out.println("Введіть номер рейсу (число): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice == 0) {
            System.out.println("Повертаємось до головного меню...");
            return;
        }
        System.out.println("0. Повернутися до головного меню");
        System.out.print("Ореріть рейс для бронювання (число): ");
        if (choice < 1 || choice > found.size()) {
            System.out.println("Некоректний вибір.");
            return;
        }
        Flight flight = found.get(choice - 1);
        System.out.print("Введіть ID пасажира: ");
        String passengerId = scanner.nextLine();
        bookingService.cancelBookingByFlightAndPassenger(flight.id(), passengerId);
    }

    public void showMyBookings(Scanner scanner) {
        System.out.print("Enter your user ID: ");
        String userId = scanner.nextLine();
        bookingService.showBookingsForUser(userId);
    }

    public void searchAndBook() {
        System.out.print("Введіть місто відпавлення: ");
        String from = scanner.nextLine();
        System.out.print("Введіть місце призначення: ");
        String to = scanner.nextLine();
        System.out.print("Введіть дату (у форматі YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Введіть число пасажирів: ");
        int passengerCount = Integer.parseInt(scanner.nextLine());

        List<Flight> found = null;
        try {
            found = flightService.searchFlights(from, to, java.time.LocalDate.parse(date), passengerCount);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        if (found.isEmpty()) {
            System.out.println("Рейс не знайдено.");
            System.out.println("Повертаємось до головного меню...");
            return;
        }
        FlightController.printFlightsWithIndexes(found);
        System.out.println("0. Повернутися до головного меню");
        System.out.print("Ореріть рейс для бронювання (число): ");
        int choice = Integer.parseInt(scanner.nextLine());
        if (choice == 0) {
            System.out.println("Повертаємось до головного меню...");
            return;
        }
        if (choice < 1 || choice > found.size()) {
            System.out.println("Некоректний вибір.");
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
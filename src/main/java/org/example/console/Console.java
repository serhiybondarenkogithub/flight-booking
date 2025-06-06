package org.example.console;

import org.example.controller.FlightController;
import org.example.controller.BookingController;
import org.example.exception.MenuChoiceException;

import java.util.Scanner;

public class Console {
    private final FlightController flightController;
    private final BookingController bookingController;
    private final Scanner scanner = new Scanner(System.in);

    public Console(FlightController flightController, BookingController bookingController) {
        this.flightController = flightController;
        this.bookingController = bookingController;
    }

    public void start() {
        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        flightController.showBoard();
                        switch (choice) {
                            case 1:
                        }
                        break;
                    case 2:
                        System.out.print("Введіть код рейсу: ");
                        String flightCode = scanner.nextLine();
                        flightController.showFlightInfo(flightCode, scanner);
                        break;
                    case 3:
                        bookingController.searchAndBook();
                        break;
                    case 4:
                        bookingController.cancelBooking(scanner);
                        break;
                    case 5:
                        bookingController.showMyBookings(scanner);
                        break;
                    case 6:
                        System.out.println("Вихід...");
                        return;
                    default:
                        throw new MenuChoiceException("Invalid menu choice!");
                }
            } catch (MenuChoiceException | NumberFormatException e) {
                System.out.println("Введіть коректний номер меню " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Головне меню ---");
        System.out.println("1. Табло рейсів");
        System.out.println("2. Показати інформацію про рейс");
        System.out.println("3. Знайти та забронювати рейс");
        System.out.println("4. Відмінити бронювання");
        System.out.println("5. Мої бронювання");
        System.out.println("6. Вихід");
        System.out.print("Введіть свій вибір: ");
    }
}
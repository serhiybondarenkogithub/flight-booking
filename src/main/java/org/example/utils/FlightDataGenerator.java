package org.example.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Scanner;

public class FlightDataGenerator {
    private static final String[] DESTINATIONS = {"Kyiv", "Lviv", "Odesa", "Kharkiv", "Dnipro"};
    private static final int FLIGHT_COUNT = 30;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int flightCount = FLIGHT_COUNT;
        System.out.print("Enter the number of flights to generate (default is 30): ");
        String input = scanner.nextLine();
        if (!input.isEmpty()) {
            try {
                flightCount = Integer.parseInt(input);
                if (flightCount <= 0) {
                    System.out.println("Invalid number, using default value of 30.");
                    flightCount = FLIGHT_COUNT;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, using default value of 30.");
            }
        }
        Random random = new Random();
        try (FileWriter writer = new FileWriter("data/flights.txt")) {
            for (int i = 1; i <= flightCount; i++) {
                String id = "FL" + String.format("%03d", i);
                String from = DESTINATIONS[random.nextInt(DESTINATIONS.length)];
                String to;
                do {
                    to = DESTINATIONS[random.nextInt(DESTINATIONS.length)];
                } while (to.equals(from));
                LocalDateTime departure = LocalDateTime.now()
                        .plusDays(random.nextInt(30))
                        .withHour(8 + random.nextInt(12))
                        .withMinute(0)
                        .withSecond(0)
                        .truncatedTo(ChronoUnit.SECONDS);
                LocalDateTime arrival = departure.plusHours(1 + random.nextInt(5));
                int seats = 50 + random.nextInt(101);
                writer.write(String.join(",",
                        id,
                        from,
                        to,
                        departure.toString(),
                        arrival.toString(),
                        String.valueOf(seats)
                ) + "\n");
            }
            System.out.println("Flights generated in data/flights.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
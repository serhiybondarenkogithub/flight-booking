package org.example.utils;

import net.datafaker.Faker;
import org.example.model.entities.Flight;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class FlightsGenerator {
    private static final Faker faker = new Faker();

    public static List<Flight> init(int num) {
        return IntStream.range(0, num).mapToObj(i -> generateFlight()).toList();
    }

    private static Flight generateFlight() {
        String id = UUID.randomUUID().toString();

        String flightCode = generateFlightCode();

        String from = faker.address().cityName();

        String to;
        do {
            to = faker.address().cityName();
        } while (to.equals(from));

        int seats = faker.number().numberBetween(100, 301);
        LocalDateTime departureDateTime = LocalDateTime.ofInstant(faker.date().future(30, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault());
        LocalDateTime arrivalDateTime = departureDateTime.plusHours(faker.number().numberBetween(1, 12));

        return new Flight(id, flightCode, from, to, departureDateTime, arrivalDateTime, seats);
    }

    private static String generateFlightCode() {
        String prefix = faker.letterify("??").toUpperCase();
        String number = String.format("%04d", faker.number().numberBetween(1, 10000));
        return prefix + number;
    }
}

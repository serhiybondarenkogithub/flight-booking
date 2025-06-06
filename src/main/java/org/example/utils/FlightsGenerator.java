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
    private static final String[] CITIES = {
            "Kyiv",         // Ukraine
            "Lviv",
            "Odesa",
            "Dnipro",
            "Kharkiv",
            "Warsaw",       // Poland
            "Krakow",
            "Prague",       // Czechia
            "Budapest",     // Hungary
            "Bucharest",    // Romania
            "Sofia",        // Bulgaria
            "Vienna",       // Austria
            "Frankfurt",    // Germany
            "Munich",
            "Paris",        // France
            "Amsterdam",    // Netherlands
            "Brussels",     // Belgium
            "Zurich",       // Switzerland
            "Copenhagen",   // Denmark
            "Stockholm",    // Sweden
            "Oslo",         // Norway
            "Helsinki",     // Finland
            "London",       // UK
            "Dublin",       // Ireland
            "Madrid",       // Spain
            "Barcelona",
            "Rome",         // Italy
            "Milan",
            "Athens",       // Greece
            "Istanbul",     // Türkiye
            "Doha",         // Qatar
            "Dubai",        // UAE
            "Abu Dhabi",
            "Delhi",        // India
            "Mumbai",
            "Tokyo",        // Japan
            "Seoul",        // South Korea
            "Singapore",    // Singapore
            "Bangkok",      // Thailand
            "New York",     // USA
            "Los Angeles",
            "Chicago",
            "Toronto",      // Canada
            "Montreal",
            "São Paulo"     // Brazil
    };

    public static List<Flight> init(int num) {
        return IntStream.range(0, num).mapToObj(i -> generateFlight()).toList();
    }

    private static Flight generateFlight() {
        String id = UUID.randomUUID().toString();

        String flightCode = generateFlightCode();

        String from = faker.bool().bool() ? "Kyiv" : faker.options().option(CITIES);;

        String to;
        do {
            to = faker.options().option(CITIES);;
        } while (to.equals(from));

        int seats = faker.number().numberBetween(100, 301);
        LocalDateTime departureDateTime = LocalDateTime.ofInstant(faker.date().future(24, TimeUnit.HOURS).toInstant(), ZoneId.systemDefault());
        LocalDateTime arrivalDateTime = departureDateTime.plusHours(faker.number().numberBetween(1, 12));

        return new Flight(id, flightCode, from, to, departureDateTime, arrivalDateTime, seats);
    }

    private static String generateFlightCode() {
        String prefix = faker.letterify("??").toUpperCase();
        String number = String.format("%04d", faker.number().numberBetween(1, 10000));
        return prefix + number;
    }
}

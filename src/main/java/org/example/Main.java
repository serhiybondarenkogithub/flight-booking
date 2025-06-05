package org.example;

import org.example.console.Console;
import org.example.controller.FlightController;
import org.example.controller.BookingController;
import org.example.service.FlightService;
import org.example.service.BookingService;
import org.example.dao.FlightDao;
import org.example.model.entities.Flight;
import org.example.dao.storage.ListStorage;
import org.example.dao.storage.FlightStorage;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ListStorage<Flight> flightStorage = new FlightStorage();
        FlightDao flightDao = new FlightDao(flightStorage, Flight.class);
        FlightService flightService = new FlightService(flightDao);
        BookingService bookingService = new BookingService(flightService);

        FlightController flightController = new FlightController(flightService);
        BookingController bookingController = new BookingController(flightService, bookingService);

        Console console = new Console(flightController, bookingController);
        console.start();
    }
}
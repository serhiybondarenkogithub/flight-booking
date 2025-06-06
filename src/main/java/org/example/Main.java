package org.example;

import org.example.console.Console;
import org.example.controller.FlightController;
import org.example.controller.BookingController;
import org.example.dao.BookingDao;
import org.example.dao.ListDao;
import org.example.dao.MapDao;
import org.example.dao.storage.FileListStorage;
import org.example.dao.storage.FileMapStorage;
import org.example.dao.storage.MapStorage;
import org.example.exception.StorageException;
import org.example.model.entities.Booking;
import org.example.service.FlightService;
import org.example.service.BookingService;
import org.example.dao.FlightDao;
import org.example.model.entities.Flight;
import org.example.dao.storage.ListStorage;
import org.example.utils.FlightsGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ListStorage<Flight> flightStorage = new FileListStorage<>(new File("data/flights.bin"));
        try { flightStorage.loadData(); } catch (StorageException se) {
            List<Flight> init = FlightsGenerator.init(30);
            try { flightStorage.saveData(init); }
            catch (StorageException e) { System.out.println("Sorry, flights database is not loaded"); }
        }
        ListDao<Flight> flightDao = new FlightDao(flightStorage, Flight.class);
        FlightService flightService = new FlightService(flightDao);
        FlightController flightController = new FlightController(flightService);

        MapStorage<Booking> bookingStorage = new FileMapStorage<>(new File("data/booking.bin"));
        try { bookingStorage.loadData(); } catch (StorageException se) {
            HashMap<String, Booking> init = new HashMap<>();
            try { bookingStorage.saveData(init); }
            catch (StorageException e) { System.out.println("Sorry, booking database is not loaded"); }
        }
        MapDao<Booking> bookingDao = new BookingDao(bookingStorage, Booking.class);
        BookingService bookingService = new BookingService(flightService, bookingDao);
        BookingController bookingController = new BookingController(flightService, bookingService);

        Console console = new Console(flightController, bookingController);
        console.start();
    }
}
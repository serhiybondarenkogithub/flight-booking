package org.example.dao.storage;

import org.example.exception.StorageException;
import org.example.model.entities.Flight;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightStorage implements ListStorage<Flight> {
    private static final String FILE_PATH = "data/flights.txt";

    @Override
    public List<Flight> loadData() throws StorageException {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;
                Flight flight = new Flight(
                        parts[0],
                        parts[1],
                        parts[2],
                        LocalDateTime.parse(parts[3]),
                        LocalDateTime.parse(parts[4]),
                        Integer.parseInt(parts[5])
                );
                flights.add(flight);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to load flights", e);
        }
        return flights;
    }

    @Override
    public void saveData(List<Flight> data) throws StorageException {
        if (data == null) {
            throw new StorageException("Flight list is null");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (Flight flight : data) {
                writer.write(String.join(",",
                        flight.id(),
                        flight.from(),
                        flight.to(),
                        flight.departureDateTime().toString(),
                        flight.arrivalDateTime().toString(),
                        String.valueOf(flight.availableSeats())
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Failed to save flights", e);
        }
    }
}
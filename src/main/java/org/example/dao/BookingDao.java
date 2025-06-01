package org.example.dao;

import org.example.model.entities.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingDao {
    List<Booking> findAll();
    Optional<Booking> findById(String id);
    void save(Booking booking);
    void deleteById(String id);
    void saveAll(List<Booking> bookings);
}
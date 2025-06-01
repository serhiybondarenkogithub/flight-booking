package org.example.dao;

import org.example.dao.storage.ListStorage;
import org.example.exception.StorageException;
import org.example.model.entities.Flight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public class FlightDaoTest {
    FlightDao dao;
    ListStorage storage;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Testing FlightDao class...");
    }

    @BeforeEach
    void setUp() {
        storage = new ListStorage() {
            List list = new ArrayList();
            @Override
            public List loadData() throws StorageException {
                new ArrayList<>();
            }

            @Override
            public void saveData(List data) throws StorageException {

            }
        }
        dao = new FlightDao();
    }
}

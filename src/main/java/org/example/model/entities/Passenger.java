package org.example.model.entities;

import org.example.model.interfaces.Identifiable;

import java.io.Serializable;
import java.util.Objects;

public class Passenger implements Identifiable, Serializable {
    private String id;
    private String firstName;
    private String lastName;

    public Passenger(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger)) return false;
        Passenger that = (Passenger) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
package com.hospital.model;

/** UML: class Physician &lt; Employee (specialty). */
public class Physician extends Employee {

    private final String specialty;

    public Physician(String name, boolean active, Clearance clearance, String specialty) {
        super(name, active, clearance);
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public boolean isClinicalStaff() {
        return true;
    }
}

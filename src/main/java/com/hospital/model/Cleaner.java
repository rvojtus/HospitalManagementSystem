package com.hospital.model;

/** UML: class Cleaner &lt; Employee. Never allowed to read patient records. */
public class Cleaner extends Employee {

    public Cleaner(String name, boolean active, Clearance clearance) {
        super(name, active, clearance);
    }

    @Override
    public boolean isClinicalStaff() {
        return false;
    }
}

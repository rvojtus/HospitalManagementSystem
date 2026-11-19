package com.hospital.model;

/** UML: class Nurse &lt; Employee. */
public class Nurse extends Employee {

    public Nurse(String name, boolean active, Clearance clearance) {
        super(name, active, clearance);
    }

    @Override
    public boolean isClinicalStaff() {
        return true;
    }
}

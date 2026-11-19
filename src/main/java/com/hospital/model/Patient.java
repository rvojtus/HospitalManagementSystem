package com.hospital.model;

import java.util.HashSet;
import java.util.Set;

/**
 * UML: class Patient (name, birthYear); associations HasRecord (1..1 PatientRecord),
 * Allergy (* Drug) and Admission (0..1 Ward).
 */
public class Patient {

    private final String name;
    private final int birthYear;
    private final PatientRecord record;
    private final Set<Drug> allergens = new HashSet<>();
    private Ward ward;

    public Patient(String name, int birthYear, PatientRecord record) {
        this.name = name;
        this.birthYear = birthYear;
        this.record = record;
    }

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public PatientRecord getRecord() {
        return record;
    }

    public Ward getWard() {
        return ward;
    }

    /** Called by {@link Ward#admit(Patient)} to keep both association ends in sync. */
    void setWard(Ward ward) {
        this.ward = ward;
    }

    public void addAllergy(Drug drug) {
        allergens.add(drug);
    }

    public boolean isAllergicTo(Drug drug) {
        return allergens.contains(drug);
    }
}

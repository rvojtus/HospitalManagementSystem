package com.hospital.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UML: class Ward (name, capacity, minDayStaff, minNightNurses, requiredSpecialty, /occupancy);
 * associations Admission (Patient[*]) and WardShift (Shift[*]).
 *
 * <pre>
 * context Ward inv capacityPositive: self.capacity > 0
 * context Ward inv notOverCapacity: self.patients->size() <= self.capacity
 * -- derived attribute: occupancy : Integer derive : self.patients->size()
 * </pre>
 */
public class Ward {

    private final String name;
    private final int capacity;
    private final int minDayStaff;
    private final int minNightNurses;
    private final String requiredSpecialty;
    private final List<Patient> patients = new ArrayList<>();
    private final List<Shift> shifts = new ArrayList<>();

    /**
     * @param requiredSpecialty specialty every shift needs, or {@code ""} for none
     */
    public Ward(String name, int capacity, int minDayStaff, int minNightNurses,
                String requiredSpecialty) {
        Contract.pre(capacity > 0, "capacityPositive");
        this.name = name;
        this.capacity = capacity;
        this.minDayStaff = minDayStaff;
        this.minNightNurses = minNightNurses;
        this.requiredSpecialty = requiredSpecialty;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMinDayStaff() {
        return minDayStaff;
    }

    public int getMinNightNurses() {
        return minNightNurses;
    }

    public String getRequiredSpecialty() {
        return requiredSpecialty;
    }

    /** Derived attribute {@code occupancy}. */
    public int getOccupancy() {
        return patients.size();
    }

    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Shift> getShifts() {
        return Collections.unmodifiableList(shifts);
    }

    /**
     * <pre>
     * context Ward::admit(p : Patient)
     *   pre  notAlreadyAdmitted: p.ward->isEmpty()
     *   pre  hasFreeBed: self.patients->size() < self.capacity
     *   post admitted: self.patients->includes(p) and self.patients->size() = self.patients@pre->size() + 1
     * </pre>
     */
    public void admit(Patient patient) {
        Contract.pre(patient.getWard() == null, "notAlreadyAdmitted");
        Contract.pre(patients.size() < capacity, "hasFreeBed");
        int sizeAtPre = patients.size();

        patients.add(patient);
        patient.setWard(this);

        Contract.post(patients.contains(patient) && patients.size() == sizeAtPre + 1, "admitted");
        Contract.inv(patients.size() <= capacity, "notOverCapacity");
    }

    /** Called by the {@link Shift} constructor to keep both association ends in sync. */
    void registerShift(Shift shift) {
        shifts.add(shift);
    }
}

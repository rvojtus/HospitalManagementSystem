package com.hospital.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UML: abstract class Employee (name, active, clearance), association ShiftStaff to Shift.
 *
 * <pre>
 * context Employee inv clinicalClearanceOnlyForClinicalStaff:
 *   (self.clearance = Clearance::CLINICAL) implies
 *     (self.oclIsKindOf(Physician) or self.oclIsKindOf(Nurse))
 *
 * context Employee inv noDoubleShiftPerDay:
 *   self.shifts->forAll(a, b | a <> b implies a.day <> b.day)
 * </pre>
 */
public abstract class Employee {

    private final String name;
    private boolean active;
    private final Clearance clearance;
    private final List<Shift> shifts = new ArrayList<>();
    private final List<AccessEvent> accessEvents = new ArrayList<>();

    protected Employee(String name, boolean active, Clearance clearance) {
        this.name = name;
        this.active = active;
        this.clearance = clearance;
        Contract.inv(clearance != Clearance.CLINICAL || isClinicalStaff(),
                "clinicalClearanceOnlyForClinicalStaff");
    }

    /** True for physicians and nurses; false for cleaners and technical support. */
    public abstract boolean isClinicalStaff();

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Clearance getClearance() {
        return clearance;
    }

    public List<Shift> getShifts() {
        return Collections.unmodifiableList(shifts);
    }

    public List<AccessEvent> getAccessEvents() {
        return Collections.unmodifiableList(accessEvents);
    }

    /** Derived attribute {@code deniedAttempts}: number of refused access attempts in the log. */
    public long getDeniedAttempts() {
        return accessEvents.stream().filter(ev -> !ev.isGranted()).count();
    }

    /** Derived attribute {@code suspicious}: three or more refused attempts. */
    public boolean isSuspicious() {
        return getDeniedAttempts() >= 3;
    }

    /** Called by {@link PatientRecord} to keep both association ends of EventActor in sync. */
    void registerAccessEvent(AccessEvent event) {
        accessEvents.add(event);
    }

    boolean worksOnDay(int day) {
        return shifts.stream().anyMatch(s -> s.getDay() == day);
    }

    /** Called by {@link Shift#addStaff(Employee)} to keep both association ends in sync. */
    void registerShift(Shift shift) {
        shifts.add(shift);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + name + ")";
    }
}

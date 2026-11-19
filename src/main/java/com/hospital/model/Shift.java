package com.hospital.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UML: class Shift (day, type, /nurseCount); associations WardShift (1 Ward) and
 * ShiftStaff (1..* Employee).
 *
 * <pre>
 * context Shift inv enoughStaffOnDayShift:
 *   self.type = ShiftType::DAY implies
 *     self.staff->select(e | e.active)->size() >= self.ward.minDayStaff
 * context Shift inv enoughNursesOnNightShift:
 *   self.type = ShiftType::NIGHT implies
 *     self.staff->select(e | e.active and e.oclIsTypeOf(Nurse))->size() >= self.ward.minNightNurses
 * context Shift inv physicianOnNightShift:
 *   self.type = ShiftType::NIGHT implies self.staff->exists(e | e.active and e.oclIsKindOf(Physician))
 * context Shift inv emergencyRoomHasPhysician / emergencyRoomNightSurgeon / wardSpecialtyCovered ...
 * </pre>
 * The staffing invariants cannot hold while a shift is still being filled (the staff list starts
 * empty), so they are exposed as the query {@link #isSafelyStaffed()} instead of being enforced
 * on every change.
 */
public class Shift {

    private final Ward ward;
    private final int day;
    private final ShiftType type;
    private final List<Employee> staff = new ArrayList<>();

    public Shift(Ward ward, int day, ShiftType type) {
        this.ward = ward;
        this.day = day;
        this.type = type;
        ward.registerShift(this);
    }

    public Ward getWard() {
        return ward;
    }

    public int getDay() {
        return day;
    }

    public ShiftType getType() {
        return type;
    }

    public List<Employee> getStaff() {
        return Collections.unmodifiableList(staff);
    }

    /** Derived attribute {@code nurseCount}. */
    public long getNurseCount() {
        return staff.stream().filter(e -> e instanceof Nurse).count();
    }

    /**
     * <pre>
     * context Shift::addStaff(e : Employee)
     *   pre  employeeActive: e.active
     *   pre  noDoubleShiftPerDay: e.shifts->forAll(s | s.day <> self.day)
     *   post staffed: self.staff->includes(e)
     * </pre>
     */
    public void addStaff(Employee employee) {
        Contract.pre(employee.isActive(), "employeeActive");
        Contract.pre(!employee.worksOnDay(day), "noDoubleShiftPerDay");

        staff.add(employee);
        employee.registerShift(this);

        Contract.post(staff.contains(employee), "staffed");
    }

    /** True if all staffing invariants of the model hold for this shift. */
    public boolean isSafelyStaffed() {
        boolean safe;
        if (type == ShiftType.DAY) {
            safe = activeCount(Employee.class) >= ward.getMinDayStaff();
        } else {
            safe = activeCount(Nurse.class) >= ward.getMinNightNurses()
                    && activeCount(Physician.class) >= 1;
        }
        if (ward instanceof EmergencyRoom) {
            safe &= activeCount(Physician.class) >= 1;
            if (type == ShiftType.NIGHT) {
                safe &= activeCount(Surgeon.class) >= 1;
            }
        }
        if (!ward.getRequiredSpecialty().isEmpty()) {
            safe &= staff.stream().anyMatch(e -> e.isActive() && e instanceof Physician p
                    && p.getSpecialty().equals(ward.getRequiredSpecialty()));
        }
        return safe;
    }

    private long activeCount(Class<? extends Employee> kind) {
        return staff.stream().filter(e -> e.isActive() && kind.isInstance(e)).count();
    }
}

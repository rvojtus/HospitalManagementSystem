package com.hospital.model;

/**
 * UML: class EmergencyRoom &lt; Ward (triageCapacity).
 *
 * <pre>
 * context EmergencyRoom inv triageCapacityPositive: self.triageCapacity > 0
 * </pre>
 * The extra staffing rules of an emergency room (physician on every shift, surgeon on every
 * night shift) are evaluated in {@link Shift#isSafelyStaffed()}.
 */
public class EmergencyRoom extends Ward {

    private final int triageCapacity;

    public EmergencyRoom(String name, int capacity, int minDayStaff, int minNightNurses,
                         String requiredSpecialty, int triageCapacity) {
        super(name, capacity, minDayStaff, minNightNurses, requiredSpecialty);
        Contract.pre(triageCapacity > 0, "triageCapacityPositive");
        this.triageCapacity = triageCapacity;
    }

    public int getTriageCapacity() {
        return triageCapacity;
    }
}

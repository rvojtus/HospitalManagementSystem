package com.hospital.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WardAndShiftTest {

    private static Patient patient(String name) {
        return new Patient(name, 1990, new PatientRecord(Clearance.BASIC, "Observation"));
    }

    // ---- Ward ----------------------------------------------------------

    @Test
    void admitUpdatesOccupancyAndBothAssociationEnds() {
        Ward icu = new Ward("ICU", 2, 2, 1, "");
        Patient p = patient("Jana");
        icu.admit(p);
        assertEquals(1, icu.getOccupancy());
        assertEquals(icu, p.getWard());
    }

    @Test
    void wardCannotBeOverCapacity() {
        Ward icu = new Ward("ICU", 1, 2, 1, "");
        icu.admit(patient("A"));
        ContractViolation v = assertThrows(ContractViolation.class, () -> icu.admit(patient("B")));
        assertEquals("hasFreeBed", v.getConstraint());
    }

    @Test
    void patientCannotBeAdmittedTwice() {
        Ward icu = new Ward("ICU", 5, 2, 1, "");
        Patient p = patient("A");
        icu.admit(p);
        ContractViolation v = assertThrows(ContractViolation.class, () -> icu.admit(p));
        assertEquals("notAlreadyAdmitted", v.getConstraint());
    }

    @Test
    void capacityMustBePositive() {
        assertThrows(ContractViolation.class, () -> new Ward("X", 0, 1, 1, ""));
    }

    // ---- Shift ---------------------------------------------------------

    @Test
    void nightShiftNeedsNurseAndPhysician() {
        Ward icu = new Ward("ICU", 2, 2, 1, "");
        Shift night = new Shift(icu, 1, ShiftType.NIGHT);
        Nurse nurse = new Nurse("Kral", true, Clearance.CLINICAL);
        Physician doc = new Physician("Svoboda", true, Clearance.CLINICAL, "Internal medicine");

        night.addStaff(nurse);
        assertFalse(night.isSafelyStaffed(), "no physician yet");
        night.addStaff(doc);
        assertTrue(night.isSafelyStaffed());
        assertEquals(1, night.getNurseCount());
    }

    @Test
    void inactiveNurseDoesNotCount() {
        Ward icu = new Ward("ICU", 2, 2, 1, "");
        Shift night = new Shift(icu, 1, ShiftType.NIGHT);
        Nurse nurse = new Nurse("Kral", true, Clearance.CLINICAL);
        night.addStaff(nurse);
        night.addStaff(new Physician("Svoboda", true, Clearance.CLINICAL, "Internal medicine"));
        nurse.setActive(false);
        assertFalse(night.isSafelyStaffed());
    }

    @Test
    void dayShiftNeedsMinimumStaff() {
        Ward icu = new Ward("ICU", 2, 2, 1, "");
        Shift day = new Shift(icu, 1, ShiftType.DAY);
        day.addStaff(new Cleaner("Dvorak", true, Clearance.NONE));
        assertFalse(day.isSafelyStaffed());
        day.addStaff(new Nurse("Kral", true, Clearance.CLINICAL));
        assertTrue(day.isSafelyStaffed());
    }

    @Test
    void nobodyWorksTwoShiftsOnTheSameDay() {
        Ward icu = new Ward("ICU", 2, 2, 1, "");
        Nurse nurse = new Nurse("Kral", true, Clearance.CLINICAL);
        new Shift(icu, 1, ShiftType.DAY).addStaff(nurse);
        Shift night = new Shift(icu, 1, ShiftType.NIGHT);
        ContractViolation v = assertThrows(ContractViolation.class, () -> night.addStaff(nurse));
        assertEquals("noDoubleShiftPerDay", v.getConstraint());
    }

    @Test
    void emergencyRoomNightShiftNeedsASurgeon() {
        EmergencyRoom er = new EmergencyRoom("ER", 10, 3, 2, "", 4);
        Shift night = new Shift(er, 2, ShiftType.NIGHT);
        night.addStaff(new Nurse("Bila", true, Clearance.CLINICAL));
        night.addStaff(new Nurse("Cerna", true, Clearance.CLINICAL));
        night.addStaff(new Physician("Svoboda", true, Clearance.CLINICAL, "Emergency medicine"));
        assertFalse(night.isSafelyStaffed(), "physician but no surgeon");

        night.addStaff(new Surgeon("Horak", true, Clearance.CLINICAL, "Trauma surgery", false));
        assertTrue(night.isSafelyStaffed());
    }

    @Test
    void specialisedWardNeedsPhysicianWithRequiredSpecialty() {
        Ward plaster = new Ward("Plaster Room", 4, 2, 0, "Orthopaedics");
        Shift day = new Shift(plaster, 3, ShiftType.DAY);
        day.addStaff(new Nurse("Zelena", true, Clearance.CLINICAL));
        day.addStaff(new Physician("Dermatolog", true, Clearance.CLINICAL, "Dermatology"));
        assertFalse(day.isSafelyStaffed());

        day.addStaff(new Physician("Prochazka", true, Clearance.CLINICAL, "Orthopaedics"));
        assertTrue(day.isSafelyStaffed());
    }
}

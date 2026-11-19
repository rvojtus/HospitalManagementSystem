package com.hospital.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessControlTest {

    private PatientRecord record;
    private Physician doctor;

    @BeforeEach
    void setUp() {
        record = new PatientRecord(Clearance.CLINICAL, "Aortic stenosis");
        doctor = new Physician("Dr. Svoboda", true, Clearance.CLINICAL, "Internal medicine");
    }

    @Test
    void clinicalStaffWithEnoughClearanceGetsAccess() {
        record.grantAccess(doctor);
        assertTrue(record.mayAccess(doctor));
        assertEquals("Aortic stenosis", record.readDiagnosis(doctor, 100));
    }

    @Test
    void cleanerNeverGetsAccess() {
        Cleaner cleaner = new Cleaner("Mr. Dvorak", true, Clearance.NONE);
        ContractViolation v = assertThrows(ContractViolation.class, () -> record.grantAccess(cleaner));
        assertEquals("noNonClinicalAccess", v.getConstraint());
    }

    @Test
    void insufficientClearanceIsRejected() {
        Nurse nurse = new Nurse("Nurse Kral", true, Clearance.BASIC);
        ContractViolation v = assertThrows(ContractViolation.class, () -> record.grantAccess(nurse));
        assertEquals("clearanceSufficient", v.getConstraint());
    }

    @Test
    void inactiveStaffIsRejected() {
        doctor.setActive(false);
        ContractViolation v = assertThrows(ContractViolation.class, () -> record.grantAccess(doctor));
        assertEquals("onlyActiveStaffHaveAccess", v.getConstraint());
    }

    @Test
    void readingWithoutAccessIsRejected() {
        assertThrows(ContractViolation.class, () -> record.readDiagnosis(doctor, 100));
    }

    @Test
    void cleanerCannotHoldClinicalClearance() {
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> new Cleaner("X", true, Clearance.CLINICAL));
        assertEquals("clinicalClearanceOnlyForClinicalStaff", v.getConstraint());
    }
}

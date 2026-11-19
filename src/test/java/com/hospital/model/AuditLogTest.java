package com.hospital.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditLogTest {

    private PatientRecord record;
    private Physician doctor;
    private Cleaner cleaner;

    @BeforeEach
    void setUp() {
        record = new PatientRecord(Clearance.CLINICAL, "Aortic stenosis");
        doctor = new Physician("Dr. Svoboda", true, Clearance.CLINICAL, "Internal medicine");
        cleaner = new Cleaner("Mr. Dvorak", true, Clearance.NONE);
        record.grantAccess(doctor);
    }

    @Test
    void grantedReadIsLogged() {
        record.readDiagnosis(doctor, 100);

        assertEquals(1, record.getAccessCount());
        AccessEvent ev = record.getAccessEvents().get(0);
        assertEquals(doctor, ev.getActor());
        assertEquals(AccessType.READ, ev.getAction());
        assertEquals(100, ev.getTime());
        assertTrue(ev.isGranted());
        assertEquals(1, doctor.getAccessEvents().size(), "both association ends must be in sync");
    }

    @Test
    void refusedAttemptIsLoggedEvenThoughTheReadFails() {
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> record.readDiagnosis(cleaner, 105));
        assertEquals("readerHasAccess", v.getConstraint());

        assertEquals(1, record.getAccessCount());
        assertFalse(record.getAccessEvents().get(0).isGranted());
        assertEquals(1, cleaner.getDeniedAttempts());
    }

    @Test
    void writeIsLoggedAndChangesTheDiagnosis() {
        record.updateDiagnosis(doctor, "Mitral stenosis", 200);

        assertEquals("Mitral stenosis", record.readDiagnosis(doctor, 201));
        assertEquals(AccessType.WRITE, record.getAccessEvents().get(0).getAction());
        assertEquals(2, record.getAccessCount());
    }

    @Test
    void threeRefusedAttemptsMakeAnEmployeeSuspicious() {
        for (int t = 1; t <= 2; t++) {
            int time = t;
            assertThrows(ContractViolation.class, () -> record.readDiagnosis(cleaner, time));
        }
        assertFalse(cleaner.isSuspicious());

        assertThrows(ContractViolation.class, () -> record.readDiagnosis(cleaner, 3));
        assertTrue(cleaner.isSuspicious());
        assertEquals(3, cleaner.getDeniedAttempts());
    }

    @Test
    void eventTimeMustNotBeNegative() {
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> new AccessEvent(doctor, record, -5, AccessType.READ, true));
        assertEquals("validEventTime", v.getConstraint());
    }

    @Test
    void grantedEventForACleanerIsRejected() {
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> new AccessEvent(cleaner, record, 10, AccessType.WRITE, true));
        assertEquals("grantedNotForNonClinicalStaff", v.getConstraint());
    }
}

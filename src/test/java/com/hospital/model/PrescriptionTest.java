package com.hospital.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrescriptionTest {

    private Physician doctor;
    private Patient patient;
    private Drug heparin;
    private Drug penicillin;

    @BeforeEach
    void setUp() {
        doctor = new Physician("Dr. Svoboda", true, Clearance.CLINICAL, "Internal medicine");
        PatientRecord record = new PatientRecord(Clearance.CLINICAL, "Aortic stenosis");
        record.grantAccess(doctor);
        patient = new Patient("Jana Maly", 1980, record);
        heparin = new Drug("Heparin", 20, 10);
        penicillin = new Drug("Penicillin", 50, 10);
        patient.addAllergy(penicillin);
    }

    @Test
    void validPrescriptionCanBeDispensed() {
        Prescription rx = new Prescription(doctor, patient, heparin, 5);
        rx.dispense();
        assertTrue(rx.isDispensed());
        assertEquals(15, heparin.getStock());
    }

    @Test
    void allergicPatientIsProtected() {
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> new Prescription(doctor, patient, penicillin, 1));
        assertEquals("noAllergen", v.getConstraint());
    }

    @Test
    void prescriberWithoutRecordAccessIsRejected() {
        Physician stranger = new Physician("Dr. Cizi", true, Clearance.CLINICAL, "Dermatology");
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> new Prescription(stranger, patient, heparin, 1));
        assertEquals("prescriberHasAccess", v.getConstraint());
    }

    @Test
    void quantityMustBePositive() {
        ContractViolation v = assertThrows(ContractViolation.class,
                () -> new Prescription(doctor, patient, heparin, 0));
        assertEquals("quantityPositive", v.getConstraint());
    }

    @Test
    void dispensingMoreThanInStockFailsOnDrugContract() {
        Prescription rx = new Prescription(doctor, patient, heparin, 25);
        ContractViolation v = assertThrows(ContractViolation.class, rx::dispense);
        assertEquals("enoughStock", v.getConstraint());
    }

    @Test
    void prescriptionCanOnlyBeDispensedOnce() {
        Prescription rx = new Prescription(doctor, patient, heparin, 5);
        rx.dispense();
        assertThrows(ContractViolation.class, rx::dispense);
    }
}

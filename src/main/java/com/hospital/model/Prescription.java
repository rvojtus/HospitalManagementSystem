package com.hospital.model;

/**
 * UML: association class Prescription between Physician, Patient and Drug (quantity).
 *
 * <pre>
 * context Prescription inv noAllergen:
 *   not self.drug.allergicPatients->includes(self.treatedPatient)
 * context Prescription inv quantityPositive: self.quantity > 0
 * context Prescription inv prescriberHasAccess:
 *   self.treatedPatient.record.authorizedStaff->includes(self.prescriber)
 * </pre>
 * Checked in the constructor, so an invalid Prescription object can never exist.
 */
public class Prescription {

    private final Physician prescriber;
    private final Patient patient;
    private final Drug drug;
    private final int quantity;
    private boolean dispensed;

    public Prescription(Physician prescriber, Patient patient, Drug drug, int quantity) {
        Contract.pre(quantity > 0, "quantityPositive");
        Contract.pre(!patient.isAllergicTo(drug), "noAllergen");
        Contract.pre(patient.getRecord().mayAccess(prescriber), "prescriberHasAccess");
        this.prescriber = prescriber;
        this.patient = patient;
        this.drug = drug;
        this.quantity = quantity;
    }

    public Physician getPrescriber() {
        return prescriber;
    }

    public Patient getPatient() {
        return patient;
    }

    public Drug getDrug() {
        return drug;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isDispensed() {
        return dispensed;
    }

    /** Dispenses the prescribed quantity; delegates the stock contract to {@link Drug#dispense(int)}. */
    public void dispense() {
        Contract.pre(!dispensed, "notYetDispensed");
        drug.dispense(quantity);
        dispensed = true;
    }
}

package com.hospital.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UML: class PatientRecord (requiredClearance, diagnosis, /accessCount), associations
 * RecordAccess to Employee and EventRecord to AccessEvent.
 *
 * <pre>
 * context PatientRecord inv onlyActiveStaffHaveAccess:
 *   self.authorizedStaff->forAll(e | e.active)
 * context PatientRecord inv noNonClinicalAccess:
 *   self.authorizedStaff->forAll(e | not (e.oclIsTypeOf(Cleaner) or e.oclIsTypeOf(TechSupport)))
 * context PatientRecord inv clearanceSufficient:
 *   self.authorizedStaff->forAll(e | e.clearance covers self.requiredClearance)
 * -- derived attribute: accessCount : Integer derive : self.accessEvents->size()
 * </pre>
 * The invariants are enforced as preconditions of {@link #grantAccess(Employee)}.
 * <p>
 * Every attempt to read or change the diagnosis is written to the audit log, also a refused one.
 * The OCL model cannot demand that ("every access leaves an entry" is a property of behaviour,
 * not of a state); here it is guaranteed by routing all access through {@link #attempt}.
 */
public class PatientRecord {

    private final Clearance requiredClearance;
    private String diagnosis;
    private final List<Employee> authorizedStaff = new ArrayList<>();
    private final List<AccessEvent> accessEvents = new ArrayList<>();

    public PatientRecord(Clearance requiredClearance, String diagnosis) {
        this.requiredClearance = requiredClearance;
        this.diagnosis = diagnosis;
    }

    public Clearance getRequiredClearance() {
        return requiredClearance;
    }

    public List<Employee> getAuthorizedStaff() {
        return Collections.unmodifiableList(authorizedStaff);
    }

    public List<AccessEvent> getAccessEvents() {
        return Collections.unmodifiableList(accessEvents);
    }

    /** Derived attribute {@code accessCount}: logged accesses, granted or refused. */
    public int getAccessCount() {
        return accessEvents.size();
    }

    public boolean mayAccess(Employee employee) {
        return authorizedStaff.contains(employee);
    }

    public void grantAccess(Employee employee) {
        Contract.pre(employee.isActive(), "onlyActiveStaffHaveAccess");
        Contract.pre(employee.isClinicalStaff(), "noNonClinicalAccess");
        Contract.pre(employee.getClearance().covers(requiredClearance), "clearanceSufficient");

        authorizedStaff.add(employee);

        Contract.post(mayAccess(employee), "accessGranted");
    }

    /**
     * Reads the diagnosis at the given time. The attempt is logged; if the reader has no access
     * the call fails after the (refused) attempt has been logged.
     */
    public String readDiagnosis(Employee reader, int time) {
        attempt(reader, AccessType.READ, time);
        return diagnosis;
    }

    /** Changes the diagnosis at the given time. Logged like {@link #readDiagnosis}. */
    public void updateDiagnosis(Employee writer, String newDiagnosis, int time) {
        attempt(writer, AccessType.WRITE, time);
        this.diagnosis = newDiagnosis;
    }

    /**
     * <pre>
     * context PatientRecord::attempt(e : Employee, a : AccessType, t : Integer)
     *   pre  readerHasAccess: self.authorizedStaff->includes(e)
     *   post logged: self.accessEvents->size() = self.accessEvents@pre->size() + 1
     * </pre>
     * The log entry is added first, then the precondition is checked, so refused attempts are
     * recorded as well.
     */
    private void attempt(Employee employee, AccessType action, int time) {
        int sizeAtPre = accessEvents.size();
        boolean granted = mayAccess(employee);

        AccessEvent event = new AccessEvent(employee, this, time, action, granted);
        accessEvents.add(event);
        employee.registerAccessEvent(event);

        Contract.post(accessEvents.size() == sizeAtPre + 1, "logged");
        Contract.pre(granted, "readerHasAccess");
    }
}

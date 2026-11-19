package com.hospital.model;

/**
 * UML: class AccessEvent (time, action, granted); associations EventActor (1 Employee) and
 * EventRecord (1 PatientRecord). One entry of the audit log: an attempt of an employee to access
 * a patient record, whether granted or refused.
 *
 * <pre>
 * context AccessEvent inv validEventTime: self.time >= 0
 * context AccessEvent inv grantedNotForNonClinicalStaff:
 *   self.granted implies
 *     not (self.actor.oclIsTypeOf(Cleaner) or self.actor.oclIsTypeOf(TechSupport))
 * </pre>
 * The class is immutable (log entries are never changed), and the invariants are checked in the
 * constructor, so an invalid entry can never exist. Entries are created by
 * {@link PatientRecord}, which also adds them to both association ends.
 */
public final class AccessEvent {

    private final Employee actor;
    private final PatientRecord record;
    private final int time;
    private final AccessType action;
    private final boolean granted;

    AccessEvent(Employee actor, PatientRecord record, int time, AccessType action, boolean granted) {
        Contract.inv(time >= 0, "validEventTime");
        Contract.inv(!granted || actor.isClinicalStaff(), "grantedNotForNonClinicalStaff");
        this.actor = actor;
        this.record = record;
        this.time = time;
        this.action = action;
        this.granted = granted;
    }

    public Employee getActor() {
        return actor;
    }

    public PatientRecord getRecord() {
        return record;
    }

    public int getTime() {
        return time;
    }

    public AccessType getAction() {
        return action;
    }

    public boolean isGranted() {
        return granted;
    }
}

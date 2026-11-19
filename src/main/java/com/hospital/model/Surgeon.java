package com.hospital.model;

/** UML: class Surgeon &lt; Physician (robotCertified). */
public class Surgeon extends Physician {

    private final boolean robotCertified;

    public Surgeon(String name, boolean active, Clearance clearance, String specialty,
                   boolean robotCertified) {
        super(name, active, clearance, specialty);
        this.robotCertified = robotCertified;
    }

    public boolean isRobotCertified() {
        return robotCertified;
    }
}

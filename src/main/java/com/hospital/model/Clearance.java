package com.hospital.model;

/** OCL: {@code enum Clearance { NONE, BASIC, CLINICAL, ADMIN }} (ordered by increasing privilege). */
public enum Clearance {
    NONE, BASIC, CLINICAL, ADMIN;

    /** True if this clearance is at least as high as {@code required}. */
    public boolean covers(Clearance required) {
        return this.ordinal() >= required.ordinal();
    }
}

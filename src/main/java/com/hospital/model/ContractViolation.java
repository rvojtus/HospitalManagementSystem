package com.hospital.model;

/** Thrown when an OCL-derived pre-condition, post-condition or invariant is violated. */
public class ContractViolation extends RuntimeException {

    private final String kind;
    private final String constraint;

    public ContractViolation(String kind, String constraint) {
        super(kind + " violated: " + constraint);
        this.kind = kind;
        this.constraint = constraint;
    }

    /** "precondition", "postcondition" or "invariant". */
    public String getKind() {
        return kind;
    }

    /** Name of the OCL constraint, e.g. {@code enoughStock}. */
    public String getConstraint() {
        return constraint;
    }
}

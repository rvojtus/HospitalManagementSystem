package com.hospital.model;

/**
 * Tiny design-by-contract helper. Each OCL pre-condition, post-condition or
 * invariant of the USE model is checked with one of these methods and
 * reported under the same name as in HospitalManagement.use.
 */
final class Contract {

    private Contract() {
    }

    /** OCL {@code pre name: ...} */
    static void pre(boolean condition, String name) {
        if (!condition) {
            throw new ContractViolation("precondition", name);
        }
    }

    /** OCL {@code post name: ...} */
    static void post(boolean condition, String name) {
        if (!condition) {
            throw new ContractViolation("postcondition", name);
        }
    }

    /** OCL {@code inv name: ...} */
    static void inv(boolean condition, String name) {
        if (!condition) {
            throw new ContractViolation("invariant", name);
        }
    }
}

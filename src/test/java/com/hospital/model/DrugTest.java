package com.hospital.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DrugTest {

    @Test
    void dispenseReducesStock() {
        Drug heparin = new Drug("Heparin", 20, 10);
        heparin.dispense(5);
        assertEquals(15, heparin.getStock());
        assertFalse(heparin.isLowStock());
    }

    @Test
    void lowStockIsDerivedFromReorderLevel() {
        Drug heparin = new Drug("Heparin", 20, 10);
        heparin.dispense(10);
        assertTrue(heparin.isLowStock());
    }

    @Test
    void dispenseRejectsNonPositiveQuantity() {
        Drug heparin = new Drug("Heparin", 20, 10);
        ContractViolation v = assertThrows(ContractViolation.class, () -> heparin.dispense(0));
        assertEquals("positiveQuantity", v.getConstraint());
    }

    @Test
    void dispenseRejectsMoreThanInStock() {
        Drug heparin = new Drug("Heparin", 3, 10);
        ContractViolation v = assertThrows(ContractViolation.class, () -> heparin.dispense(5));
        assertEquals("enoughStock", v.getConstraint());
        assertEquals(3, heparin.getStock(), "failed call must not change the stock");
    }

    @Test
    void negativeStockIsRejected() {
        ContractViolation v = assertThrows(ContractViolation.class, () -> new Drug("X", -1, 0));
        assertEquals("stockNonNegative", v.getConstraint());
    }
}

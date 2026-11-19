package com.hospital.model;

/**
 * UML: class Drug (name, stock, reorderLevel, /lowStock) with operation dispense(q).
 *
 * <pre>
 * context Drug inv stockNonNegative: self.stock >= 0
 * -- derived attribute: lowStock : Boolean derive : self.stock <= self.reorderLevel
 *
 * context Drug::dispense(q : Integer)
 *   pre  positiveQuantity: q > 0
 *   pre  enoughStock: self.stock >= q
 *   post stockReduced: self.stock = self.stock@pre - q
 * </pre>
 */
public class Drug {

    private final String name;
    private int stock;
    private final int reorderLevel;

    public Drug(String name, int stock, int reorderLevel) {
        this.name = name;
        this.stock = stock;
        this.reorderLevel = reorderLevel;
        checkInvariant();
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    /** Derived attribute {@code lowStock}. */
    public boolean isLowStock() {
        return stock <= reorderLevel;
    }

    public void dispense(int q) {
        Contract.pre(q > 0, "positiveQuantity");
        Contract.pre(stock >= q, "enoughStock");
        int stockAtPre = stock;

        stock -= q;

        Contract.post(stock == stockAtPre - q, "stockReduced");
        checkInvariant();
    }

    /** Adds delivered units to the stock. */
    public void restock(int q) {
        Contract.pre(q > 0, "positiveQuantity");
        stock += q;
        checkInvariant();
    }

    private void checkInvariant() {
        Contract.inv(stock >= 0, "stockNonNegative");
    }
}

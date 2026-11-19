# Prototype implementation (Task 5)

## Scope

The prototype implements the part of the class diagram that carries most of the OCL: personnel, wards and shifts, patient records with access control and the audit log, and drugs with prescriptions.

## Approach and tooling

We have used Claude Sonnet 5.5 (Anthropic) to generate the prototype implementation. We have used our `HospitalManagement.use` model (class diagram with OCL constraints) as input, while specifying in the prompt to generate the prototype in Java Maven style.

## Quality of the generated code

The AI-tool has in fact generated Java Classes with Maven style pom.xml. There are also Javadoc-style comments in each generated class. As a prototype or a starting point, the classes seem alright, they represent the UML Classes quite well. The AI has also generated some tests, which was unexpected as it was not prompted to do so. They seem to be implemented in JUnit style.

## Relation of code and specification

The code follows the specification closely. Every class quotes its OCL constraints in the Javadoc, and a small helper class `Contract` checks them and reports a violation under the same name as in `HospitalManagement.use`, for example `noAllergen` or `enoughStock`.

- **Pre- and postconditions** (`Drug::dispense`) are checked at the start and end of the method.
- **Invariants** are checked in the constructor and after changes. For association classes (`Prescription`, `AccessEvent`) the constructor check means an invalid object never exists.
- **Derived attributes** (`occupancy`, `lowStock`, ...) are getters that compute the value on every call.

Where code and specification differ:

- **Java has no invariants.** In OCL an invariant holds in every state. In Java it is checked only where the code calls it, so any other change of state can break it without warning.
- **The staffing invariants of `Shift` are only a query** (`isSafelyStaffed()`), because a new shift has no staff and the rules cannot hold while it is being filled.
- **The audit log depends on a convention.** OCL cannot say "every access leaves a log entry", so the code guarantees it only because all access goes through `PatientRecord.attempt()`.
- **Associations are automatic in OCL but manual in Java.** Both ends must be updated by hand.

Nothing in the prototype detects when code and specification drift apart.

## Mistakes made by the AI

It generated all classes into a single Java package, which is not ideal.

A review of the generated code found more problems. The following list was produced by an AI tool that was asked to check the code for bugs and mistakes. The main points were confirmed by running the code:

**Bugs**
- Access list is a `List`, so the same employee can be granted access twice (the model uses a set).
- Invariants are checked only when an object is built or a method is called. Deactivating an employee with `setActive(false)` leaves them on a record's access list, and adding an allergy after a prescription breaks `noAllergen`, both without any error.
- Contract checks run after the state is changed and nothing is rolled back. `Drug.restock(Integer.MAX_VALUE)` overflows and leaves a negative stock.
- An access attempt with an invalid time is rejected before it is logged, so it leaves no audit entry.

**Differences from the model**
- `TechSupport`, the devices, `BasicEquipment` and `DeviceBooking` are missing.
- `AccessEvent` checks `isClinicalStaff()` instead of "not Cleaner and not TechSupport", which is only equal while those are the only non-clinical roles.
- Some invariants became preconditions (`noDoubleShiftPerDay`, the `Prescription` rules), and the `1..*` multiplicity of shift staff is not enforced.
- `Prescription` objects are not stored on patient, drug or physician, so the ternary relation cannot be navigated.

**Weaker points**
- `Employee`'s constructor calls an abstract method.
- `Clearance.covers` depends on the declaration order of the enum.
- Some postconditions (`stockReduced`, `logged`) cannot fail.

## Conclusion

We believe that for smaller systems it is sufficient to generate the structure or prototype with AI tools. The generated code compiled, followed the model closely and passed its tests. On the other hand, it is not sufficient for serious real-world projects. The review showed that the invariants are checked only where the code calls them, and that the code lacks the architectural decisions a software architect would make. 

Model-driven development already addresses this: for example, the Eclipse Modeling Framework takes a class diagram and generates a Java project, while keeping cardinalities, relations and so on consistent with the model.

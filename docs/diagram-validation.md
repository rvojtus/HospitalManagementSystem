# Validation of the UML diagrams in practice (Task 3)

## Syntax and consistency checks

USE checks the syntax of the model file, type-checks every OCL expression and checks the structure of the classes and associations. We did this by loading `HospitalManagement.use`. It found real mistakes, for example a role name used twice and an expression that treated one object as a collection.

It cannot tell whether a constraint says the right thing, or whether two constraints contradict each other. For that the model must be tried on example states.

## Checking the constraints with example states

The script `scenario.soil` first builds a valid state and checks that no invariant fails. Then it applies deliberate violations (for example a cleaner reading a record, an allergic prescription, a night shift without a nurse) and checks that the intended invariants report them. The contract of `Drug::dispense` was checked with `openter` and `opexit`.

This tests both directions: valid states must be accepted (otherwise the model is too strict), and invalid states must be rejected (otherwise it is too permissive).

## Review with domain experts

All relevant models and concepts must be reviewed in cooperation with domain experts. In our domain, hospital management, we should have discussions with doctors, nurses, hospital administrators, and the people responsible for security and IT. Each party should give their insight into the proposed models, but the discussion must be controlled: the right experts need to be asked the right questions.

Experts usually cannot read UML or OCL, so we would translate the invariants into plain sentences and show example states, asking "is this allowed in your hospital?"

## Validating the sequence diagram

We would check each message of the sequence diagram against the class model: it should use existing classes and operations, and show the same rules as the OCL.

## Limits of validation

Not every aspect of the system we are trying to capture and model can be validated. Often the systems and scenarios are too complex to validate as a whole. Some cases require secrecy, so as not to expose internal security processes or intellectual property. Experts may also disagree, and real rules change over time. Tests can show that a mistake exists, but never that there is none.

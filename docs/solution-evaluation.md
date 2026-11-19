# Evaluation of the solution (Task 4)

## 1. Level of abstraction

A lot of details had to be ignored or abstracted on purpose, as going too deep into specifics would muddy the water and make the model confusing. We have ignored specifics of drug prescriptions, such as expiration dates and batches. Another big topic is legal requirements: we did not specify any law-related details.

Other things that were left out or simplified:

- **Time:** real dates and calendars. Times are plain integers and shifts are a day number plus `DAY` or `NIGHT`.
- **People and patients:** no qualifications, sick leave, appointments, transfers or discharge dates.
- **Places and equipment:** no rooms or beds, no maintenance of devices.
- **Money:** no billing or insurance.

This level is reasonable because the model is meant to show the safety and security rules.

## 2. Level of approximation

### 2.1 Over-specification (too restrictive)

Some of our specifications might seem too restrictive in comparison with real world hospitals. For example, we have limited the number of shifts a medical worker can work in a single day, but as we know, it is not uncommon for a nurse to work multiple consecutive shifts.

Other examples:

- **Fixed minimum staffing:** in reality it depends on the day and on the number of patients.
- **Surgeon at night in the emergency room:** too strict for a small hospital where the surgeon is on call.
- **Access before prescribing or booking:** a physician without access to the record cannot act, even in an emergency.

### 2.2 Under-specification (too permissive)

Clearance levels need to be specified with more details and logic. Right now there is one level per employee and one per record. A nurse could read the record of a patient from another ward, and there is no rule for emergency access.

Other examples:

- **Drug interactions:** only allergies are checked.
- **Stock:** does not reflect real state.
- **Double booking:** a physician or a patient can be in two bookings at the same time.
- **Shift timing:** no rules about rest or maximum working hours.

## 3. Ambiguity versus precision

The precise parts are the multiplicities, the enumerations, the types and the OCL invariants and contracts. They have an exact meaning and were checked in USE.

Parts that are still open to interpretation:

- **Names:** `active` can mean employed, on duty or available. `specialty` is free text, and the unit of the time attributes is only in a comment.
- **Clearance order:** `NONE < BASIC < CLINICAL < ADMIN` is only a comment, because an OCL enumeration has no order.

## 4. Completeness

| Scenario | Covered? | Notes |
|----------|----------|-------|
| Staffing of shifts and the emergency room | Yes | `Shift`, `EmergencyRoom`, invariants |
| Prescribing a drug | Yes | `Prescription`, access and allergy rules |
| Dispensing and reordering drugs | Partly | stock contract and `lowStock` flag, no order process |
| Booking a device | Yes | `DeviceBooking`, scheduling rules |
| Surgery with the robot | Partly | only the booking, no procedure |
| Access control and audit log | Yes | clearance, role rule, `AccessEvent` |
| Emergency access to a record | No | missing |
| Admission and transfer of patients | Partly | only a link to a ward, no transfers |
| Drug interactions, staff replacement | No | missing |
| Billing, appointments | No | out of scope |

The model is complete for its main purpose, the safety and security rules, but not for every scenario of a real hospital. The missing ones are either left out on purpose (billing, law) or are the next things to add (emergency access, transfers, drug interactions).

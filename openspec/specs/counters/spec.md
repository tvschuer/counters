# counters Specification

## Purpose
TBD - created by archiving change add-counter-creation. Update Purpose after archive.
## Requirements
### Requirement: Counter Identity
The system SHALL assign each Counter a system-generated unique identifier (UUID).

#### Scenario: Counter ID is generated on creation
- **WHEN** a counter is created
- **THEN** the system generates a UUID identifier for the counter
- **AND** the UUID is returned to the caller as the counter’s identifier

### Requirement: Create Counter
The system SHALL allow users to create a Counter with a name and a unit, and optionally a default update amount.

If provided, the default update amount MUST be persisted on the Counter.

#### Scenario: Create counter succeeds with valid name and unit
- **WHEN** a user provides a non-empty name and a non-empty unit
- **THEN** the system creates a counter
- **AND** returns the counter’s UUID identifier

#### Scenario: Default update amount is 1 when omitted
- **WHEN** a user creates a counter without specifying a default update amount
- **THEN** the counter default update amount is 1

#### Scenario: Create counter fails when name already exists
- **WHEN** a user attempts to create a counter with a name that already exists
- **THEN** the system rejects the request with a validation error indicating the name must be unique

#### Scenario: Create counter fails with missing name
- **WHEN** a user attempts to create a counter with an empty name
- **THEN** the system rejects the request with a validation error

#### Scenario: Create counter fails with missing unit
- **WHEN** a user attempts to create a counter with an empty unit
- **THEN** the system rejects the request with a validation error

### Requirement: Counter Name Is User-Facing
The system SHALL treat a Counter’s name as user-facing display text, not as the Counter’s identifier.

#### Scenario: Counter identifier is not derived from name
- **WHEN** a user creates a counter with any valid name
- **THEN** the system assigns a UUID identifier that is not derived from the name

### Requirement: Counter Value
The system SHALL track a current integer value for each Counter.

#### Scenario: New counters start at zero
- **WHEN** a counter is created
- **THEN** its current value is 0

### Requirement: Counter Default Update Amount
The system SHALL track a default update amount for each Counter.

#### Scenario: Default update amount is 1 when omitted during creation
- **WHEN** a counter is created without specifying a default update amount
- **THEN** the counter default update amount is 1

### Requirement: Update Counter Value
The system SHALL allow users to update a Counter’s current value by applying a signed integer delta.

The update operation MUST support:
- An action that indicates whether the update is an increment or decrement
- An optional `amount` parameter (integer)
- An optional `occurredAt` timestamp (UTC)

If `amount` is omitted, the system MUST use the Counter’s default update amount.

If `amount` is 0, the update MUST be treated as a no-op.

#### Scenario: Update applies a specific amount
- **GIVEN** an existing counter with value 0
- **WHEN** the user updates the counter with amount 3
- **THEN** the counter value becomes 3

#### Scenario: Update falls back to the counter default amount
- **GIVEN** an existing counter with value 0 and default update amount 5
- **WHEN** the user updates the counter without specifying an amount
- **THEN** the counter value becomes 5

#### Scenario: Decrement applies a negative delta
- **GIVEN** an existing counter with value 10 and default update amount 1
- **WHEN** the user decrements the counter with amount 3
- **THEN** the counter value becomes 7

#### Scenario: Amount 0 is a no-op
- **GIVEN** an existing counter with value 10
- **WHEN** the user increments or decrements the counter with amount 0
- **THEN** the counter value remains 10

### Requirement: Counter Update Events
The system SHALL persist an immutable CounterEvent for each counter update.

Each CounterEvent MUST include:
- The Counter identifier
- A signed integer delta
- A timestamp in UTC indicating when the update occurred (`occurredAt`)

#### Scenario: Update creates an event using server time when `occurredAt` is omitted
- **GIVEN** an existing counter
- **WHEN** the user updates the counter without specifying `occurredAt`
- **THEN** the system persists a CounterEvent linked to the counter
- **AND** the CounterEvent includes the delta applied
- **AND** the CounterEvent includes a UTC timestamp assigned by the server

#### Scenario: Update persists the provided `occurredAt` timestamp
- **GIVEN** an existing counter
- **WHEN** the user updates the counter with an explicit `occurredAt` timestamp
- **THEN** the system persists a CounterEvent linked to the counter
- **AND** the CounterEvent includes the delta applied
- **AND** the CounterEvent stores the provided `occurredAt` timestamp in UTC

#### Scenario: Amount 0 does not create an event
- **GIVEN** an existing counter
- **WHEN** the user updates the counter with amount 0
- **THEN** the system does not persist a CounterEvent

### Requirement: Soft-Delete Counter
The system SHALL allow users to soft-delete a Counter.

When a Counter is soft-deleted:
- The Counter MUST be marked with a `deletedAt` timestamp (UTC) indicating when it was deleted
- The Counter MUST be excluded from list queries
- All CounterEvents associated with the Counter MUST be preserved
- The Counter MUST NOT be physically removed from the database

#### Scenario: Soft-delete marks counter with deletedAt timestamp
- **GIVEN** an existing counter
- **WHEN** the user deletes the counter
- **THEN** the counter is marked with a `deletedAt` timestamp assigned by the server
- **AND** the counter is no longer visible in list queries

#### Scenario: Soft-deleted counters are excluded from list queries
- **GIVEN** a counter that has been soft-deleted
- **WHEN** the user lists all counters
- **THEN** the soft-deleted counter is not included in the results

#### Scenario: Counter events are preserved after soft-delete
- **GIVEN** a counter with existing CounterEvents
- **WHEN** the user deletes the counter
- **THEN** all CounterEvents associated with the counter remain in the database

### Requirement: Prevent Updates to Soft-Deleted Counters
The system SHALL reject update operations (increment/decrement) on soft-deleted Counters.

#### Scenario: Increment rejects soft-deleted counter
- **GIVEN** a counter that has been soft-deleted
- **WHEN** the user attempts to increment the counter
- **THEN** the system rejects the request with an error indicating the counter is deleted

#### Scenario: Decrement rejects soft-deleted counter
- **GIVEN** a counter that has been soft-deleted
- **WHEN** the user attempts to decrement the counter
- **THEN** the system rejects the request with an error indicating the counter is deleted


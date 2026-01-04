## ADDED Requirements

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


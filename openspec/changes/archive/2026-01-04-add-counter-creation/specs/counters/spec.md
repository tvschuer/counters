## ADDED Requirements

### Requirement: Counter Identity
The system SHALL assign each Counter a system-generated unique identifier (UUID).

#### Scenario: Counter ID is generated on creation
- **WHEN** a counter is created
- **THEN** the system generates a UUID identifier for the counter
- **AND** the UUID is returned to the caller as the counter’s identifier

### Requirement: Create Counter
The system SHALL allow users to create a Counter with a name and a unit.

#### Scenario: Create counter succeeds with valid name and unit
- **WHEN** a user provides a non-empty name and a non-empty unit
- **THEN** the system creates a counter
- **AND** returns the counter’s UUID identifier

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



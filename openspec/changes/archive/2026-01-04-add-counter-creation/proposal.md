# Change: Add Counter Creation

## Why
Tracker is a general-purpose counter system, but it currently has no defined capability for creating a counter.

## What Changes
- Define a `counters` capability with requirements to create a Counter.
- Introduce a system-generated unique identifier (UUID) for each Counter.
- Clarify that a Counter’s human-readable name is not its identifier.

## Impact
- Affected specs: (new) `counters`
- Affected code (future): domain model (Counter), persistence schema, API/UI for creation



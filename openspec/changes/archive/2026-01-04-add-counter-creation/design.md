## Context
The core domain object in Tracker is a Counter. Counters are user-facing entities with a human-readable name and unit, and will be referenced later by events (increments/decrements).

This change establishes the foundational identity model for Counters.

## Goals / Non-Goals
- Goals:
  - Introduce a stable, system-generated identifier for Counters (UUID).
  - Ensure Counter creation is well-defined with clear validation behavior.
- Non-Goals:
  - Defining increment/decrement behavior (handled in a separate change).
  - Defining counter event history persistence (handled in a separate change).

## Decisions
- Decision: Use a UUID as the Counter identifier.
  - Rationale: Names are user-facing and may change; IDs must be stable and collision-resistant.
  - Implication: All external references (API/UI links, events) should use the UUID, not the name.

- Decision: Counter name is not the identifier.
  - Rationale: Names are user-facing and may change; using them as identifiers would make renames breaking.

- Decision: Counter names are unique.
  - Rationale: Simplifies UX (no ambiguity when selecting a counter by name) while still using UUID for stable identity.

## Open Questions
- None (for this change).



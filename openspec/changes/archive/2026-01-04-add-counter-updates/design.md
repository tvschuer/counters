## Context
The current app persists counters (id, name, unit, createdAt) but does not persist a counter value or any history of updates.

The project context (`openspec/project.md`) already calls out a need for immutable counter events with timestamps to support a later dashboard/timeline view.

## Goals / Non-Goals
- Goals:
  - Support incrementing/decrementing counters over time.
  - Persist an immutable event record for each update, including a timestamp.
  - Define clear semantics for “current value”.
- Non-Goals (for this change):
  - Dashboard visualizations and analytics.
  - Multi-user ownership/auth.
  - Editing/deleting past events (immutability is intentional).

## Decisions
### Decision: Represent changes as an immutable event log
- Each update produces a `CounterEvent` containing a signed integer delta and an `occurredAt` timestamp.
- Rationale: event log enables later aggregation (daily totals, charts) without schema redesign.

### Decision: Support optional caller-provided `occurredAt`, otherwise use server time (UTC)
- If `occurredAt` is provided by the caller, persist that timestamp (normalized to UTC).
- If `occurredAt` is omitted, use server time (UTC) when the update is processed.
- Rationale: enables backdating while keeping a simple default path.

### Decision: Updates accept an optional amount and fall back to a per-counter default amount
- Update requests may include an `amount`.
- If omitted, use the counter’s `defaultAmount` (configured at creation; default = 1).
- Rationale: supports both “tap to add 1” and “add 5” without extra configuration per request.

### Decision: Decrease is modeled as a negative delta (with explicit increment/decrement actions at the boundary)
- At the domain/storage level, an update is a signed integer `delta`.
- At the API/UI boundary, we expose clear “increment” and “decrement” actions that compute `delta` as `+amount` or `-amount`.
- Rationale: keeps the core model minimal while making the external contract unambiguous.

### Decision: `amount=0` is a no-op (no state change, no event)
- If an update request results in `delta=0`, the system treats it as a no-op:
  - Counter value remains unchanged
  - No CounterEvent is persisted

### Decision (tentative): Store current value in `counters` and also store events
- Maintain a denormalized `value` on the counter row, updated transactionally alongside inserting the event.
- Rationale: simple/fast reads for list/detail views, while preserving event history for dashboards.

## Alternatives considered
- Compute current value as `SUM(delta)` of events:
  - Pros: single source of truth; no double-write.
  - Cons: more complex queries for list views; can be slower as event volume grows.

## Risks / Trade-offs
- Double-write consistency (counter.value + event insert):
  - Mitigation: perform both in one DB transaction; lock the counter row when updating value.
- Concurrency:
  - Mitigation: rely on database transaction isolation and row-level locking.

## Migration Plan (apply stage)
- Add `counter_events` table with:
  - `id` (UUID, PK)
  - `counter_id` (FK to counters)
  - `delta` (integer)
  - `occurred_at` (timestamptz, not null)
  - `created_at` (timestamptz, not null, default now())
- Add `value` column to `counters` (integer/bigint) default 0 (if we keep denormalized value).
- Add `default_amount` column to `counters` (integer/bigint) default 1.
- Add indexes for common queries:
  - `(counter_id, occurred_at desc)` on events

## Open Questions
- None.



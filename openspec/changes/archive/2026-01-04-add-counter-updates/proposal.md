# Change: Add counter updates (increment/decrement) with timestamped event log

## Why
Counters are only useful if they can change over time. We also need an immutable history of changes (with timestamps) to support later dashboard/timeline views.

## What Changes
- Add the ability to **increment** and **decrement** a counter’s value by a specific amount.
- Add a per-counter **default update amount** configured at counter creation (default = 1) that is used when an update omits an amount.
- Persist an immutable **CounterEvent** for every update, including:
  - counter id
  - delta (+/-)
  - `occurredAt` timestamp (UTC), caller-provided when supplied; otherwise server-assigned
- Define the semantics of “current counter value” (initial value + how updates affect it).

## Impact
- Affected specs:
  - `openspec/specs/counters/spec.md` (delta in this change)
- Affected code (expected during apply stage):
  - DB schema: new events table + possibly a `value` column on `counters`
  - Service layer: new update operation with transactional consistency
  - API + UI: endpoints/actions to trigger increment/decrement and show current value

## Open Questions (need decision before apply)
- None.



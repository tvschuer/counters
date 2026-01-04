# Change: Add soft-delete for counters

## Why
Users need to be able to remove counters they no longer use, but we must preserve all historical data (counter events) for reporting and analytics. A soft-delete approach marks counters as deleted without physically removing them from the database.

## What Changes
- Add a `deletedAt` timestamp (nullable) to the Counter entity
- Add a **delete counter** operation that sets `deletedAt` to the current server time
- Modify **list counters** to exclude soft-deleted counters by default
- Preserve all counter events and historical data when a counter is soft-deleted
- Prevent updates (increment/decrement) to soft-deleted counters

## Impact
- Affected specs:
  - `openspec/specs/counters/spec.md` (delta in this change)
- Affected code (expected during apply stage):
  - DB schema: add `deleted_at` column to `counters` table
  - Service layer: new delete operation + filter logic in list queries
  - Repository: update `findAllByOrderByNameAsc` to exclude deleted counters
  - API + UI: delete endpoint/action

## Open Questions
- None.


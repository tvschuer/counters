## Context
The current app does not support deleting counters. Users may want to remove counters they no longer use, but we must preserve all historical data (counter events with timestamps) for later analytics and dashboards.

## Goals / Non-Goals
- Goals:
  - Allow users to soft-delete counters (mark as deleted without physical removal)
  - Hide soft-deleted counters from list views and normal operations
  - Preserve all counter events and historical data when a counter is deleted
  - Prevent updates to soft-deleted counters
- Non-Goals (for this change):
  - Hard-delete (physical removal from database)
  - Undelete/restore functionality
  - Archiving or exporting deleted counter data
  - Cascading delete of counter events

## Decisions
### Decision: Use a nullable `deletedAt` timestamp for soft-delete
- Add a `deleted_at` column (TIMESTAMPTZ, nullable) to the `counters` table.
- When `deleted_at` IS NULL, the counter is active.
- When `deleted_at` IS NOT NULL, the counter is soft-deleted.
- Rationale: standard soft-delete pattern; preserves audit trail of when deletion occurred.

### Decision: Filter soft-deleted counters from list queries by default
- Modify `CounterRepository.findAllByOrderByNameAsc()` to exclude counters where `deleted_at IS NOT NULL`.
- Rationale: keeps list views clean; soft-deleted counters are effectively "invisible" to normal operations.

### Decision: Prevent updates to soft-deleted counters
- `increment` and `decrement` operations MUST reject soft-deleted counters with an error.
- Rationale: soft-deleted counters are logically "gone"; allowing updates would be confusing.

### Decision: Preserve counter events when a counter is soft-deleted
- Do NOT cascade delete counter events.
- Rationale: events are immutable historical records; they remain queryable for analytics even after the counter is deleted.

### Decision: Use server-assigned timestamp for `deletedAt`
- The delete operation uses the server clock (UTC) to set `deletedAt`.
- Rationale: consistent with how we handle `createdAt` and event timestamps; avoids client time-zone issues.

## Alternatives considered
- Hard-delete (physical removal):
  - Pros: simpler schema; no need for filtering logic.
  - Cons: loses all historical data; cannot support "undelete" or analytics on deleted counters.
- Add an `is_deleted` boolean flag:
  - Pros: simpler query logic (WHERE is_deleted = false).
  - Cons: loses audit trail of when deletion occurred; timestamp is more flexible for future features.

## Risks / Trade-offs
- Query performance:
  - Every list query now includes a `WHERE deleted_at IS NULL` filter.
  - Mitigation: add a partial index on `deleted_at` if needed; single-user app unlikely to have performance issues.
- Accidental visibility of deleted counters:
  - If we forget to filter `deleted_at` in a query, deleted counters may appear.
  - Mitigation: centralize filtering logic in the repository layer; add tests to verify deleted counters are excluded.

## Migration Plan (apply stage)
- Add `deleted_at` column to `counters` table:
  - `deleted_at TIMESTAMPTZ NULL`
  - Default NULL (all existing counters are active)
- Update `CounterRepository.findAllByOrderByNameAsc()` to filter `deleted_at IS NULL`
- Add `CounterService.deleteCounter(counterId)` operation
- Add API endpoint: `DELETE /api/counters/{id}` or `POST /api/counters/{id}/delete`
- Add UI delete action (button or link in counter list)

## Open Questions
- None.


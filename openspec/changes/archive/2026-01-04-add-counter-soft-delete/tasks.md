## 1. Implementation
- [x] 1.1 Add DB migration for `deleted_at` column on `counters` table
- [x] 1.2 Add `deletedAt` field to Counter domain model and CounterEntity
- [x] 1.3 Update `CounterRepository.findAllByOrderByNameAsc()` to exclude soft-deleted counters
- [x] 1.4 Implement `CounterService.deleteCounter(counterId)` operation
- [x] 1.5 Update `increment` and `decrement` to reject soft-deleted counters
- [x] 1.6 Add API endpoint for delete operation (DELETE /api/counters/{id})
- [x] 1.7 Add delete action to web UI (button in counter list)

## 2. Validation
- [x] 2.1 Unit tests for soft-delete semantics (deleted counters excluded from list, updates rejected)
- [x] 2.2 Integration tests for API delete endpoint
- [x] 2.3 Verify counter events remain queryable after counter is deleted
- [ ] 2.4 Manual smoke test via web UI


## 1. Implementation
- [x] 1.1 Add DB migration for counter value + counter default amount + counter events table
- [x] 1.2 Add persistence model for `CounterEvent`
- [x] 1.3 Extend domain to expose current value + default amount + event semantics
- [x] 1.4 Extend counter creation flow to accept optional default amount (default = 1)
- [x] 1.5 Implement service operation to update a counter transactionally:
  - apply optional amount (fallback to counter default)
  - persist event with occurredAt (caller-provided or server-assigned)
- [x] 1.6 Expose update operation via API endpoints
- [x] 1.7 Update web UI to show current value and provide increment/decrement actions (with optional amount)

## 2. Validation
- [x] 2.1 Unit tests for value update semantics
- [x] 2.2 Integration tests for API update endpoints (including amount fallback + event persistence + timestamps)
- [ ] 2.3 Manual smoke test via web UI



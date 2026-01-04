-- Add deleted_at column for soft-delete functionality
ALTER TABLE counters
  ADD COLUMN deleted_at TIMESTAMPTZ NULL;

-- Add index to improve query performance when filtering active counters
CREATE INDEX ix_counters_deleted_at ON counters (deleted_at) WHERE deleted_at IS NULL;


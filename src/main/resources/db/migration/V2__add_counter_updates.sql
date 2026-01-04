-- Add value and default_amount columns to counters table
ALTER TABLE counters
  ADD COLUMN value BIGINT NOT NULL DEFAULT 0,
  ADD COLUMN default_amount INTEGER NOT NULL DEFAULT 1;

-- Create counter_events table for immutable update history
CREATE TABLE counter_events (
  id UUID PRIMARY KEY,
  counter_id UUID NOT NULL REFERENCES counters(id) ON DELETE CASCADE,
  delta BIGINT NOT NULL,
  occurred_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Index for querying events by counter, ordered by when they occurred
CREATE INDEX ix_counter_events_counter_occurred ON counter_events (counter_id, occurred_at DESC);


CREATE TABLE counters (
  id UUID PRIMARY KEY,
  name TEXT NOT NULL,
  unit TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Enforce case-insensitive uniqueness for counter names.
CREATE UNIQUE INDEX ux_counters_name_lower ON counters (lower(name));



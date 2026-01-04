# Project Context: Tracker

## Purpose
Tracker is a **general-purpose counter system**: you can define counters (name + unit) and increment/decrement them over time.

- **Primary goal**: make it trivial to create and use counters for “anything” without hard-coding domain-specific entities.
- **Non-goals (for now)**: complex dashboards, heavy UI frameworks, premature multi-service architecture, or hosting/platform concerns beyond “Docker-ready”.

## Current State
- **Repo status**: OpenSpec scaffolding only (no app code yet).
- **Workflow**: Specs (`openspec/specs/`) are the source of truth; proposals live in `openspec/changes/`.

## Tech Stack
- **Backend**: Kotlin (JVM) + Spring Boot (latest stable)
- **Database**: Postgres
- **Frontend**: “low weight” UI (keep it simple; optimize aesthetics later)
  - Recommended baseline: server-rendered HTML + minimal JS (or HTMX) to avoid heavy SPA complexity early.
- **Containerization**: Docker required (hosting later); local dev is fine.

Notes:
- Use the **latest stable Spring Boot** (4.x as of 2025-12-18). See: https://spring.io/blog/2025/12/18/spring-boot-4-0-1-available-now

## Repository Layout & Documentation
- **Specs (built truth)**: `openspec/specs/<capability>/spec.md`
- **Proposals (planned work)**: `openspec/changes/<change-id>/`
  - `proposal.md`, `tasks.md`, optional `design.md`, plus delta specs under `specs/`
- **Archives**: `openspec/changes/archive/YYYY-MM-DD-<change-id>/`

## Project Conventions

### OpenSpec Usage
- **When to create a change proposal**: new capabilities, breaking changes, architecture shifts, perf/security work.
- **When to implement directly**: bugfixes restoring intended behavior, formatting/typos/comments, non-breaking config/dependency updates.
- **Approval gate**: don’t implement proposals until reviewed/approved.

### Code Style (Kotlin/JVM oriented)
General conventions (apply regardless of language):
- **Prefer boring, explicit code** over clever abstractions.
- **Name things by domain intent** (avoid generic `Manager`, `Helper`, `Util`).
- **Keep modules small**; split only when cohesion is lost (not preemptively).
- **Error handling**: fail fast at boundaries; keep core logic pure where practical.

Suggested enforcement once code exists:
- **Kotlin**: ktlint/spotless for formatting; detekt for static analysis (keep rules pragmatic).

### Architecture Patterns (defaults)
Until the product stabilizes, keep architecture minimal:
- **Single deployable** (unless clear scaling/ownership needs show up).
- **Hexagonal-ish boundaries** (UI/API → application/service → domain → persistence/adapters), but don’t overformalize.
- **Domain-first modeling**: specs should define domain terms and behaviors before choosing storage/API shapes.
- **Versioning**: treat externally consumed APIs/schemas as versioned contracts.

Initial core domain objects (expected):
- **Counter**: defined by **name** and **unit**
- **CounterValue**: current value (likely numeric)
- **CounterEvent**: immutable event for each increment/decrement, including a **timestamp** (enables a timeline later)

### Testing Strategy (defaults)
- **Test pyramid**:
  - Unit tests for pure domain logic
  - Integration tests for DB + API boundaries
  - Minimal E2E/UI tests for core flows only
- **Spec alignment**: each requirement scenario should map to at least one automated test once implemented.
- **CI expectation** (once CI exists): formatting/lint/typecheck + tests on every PR.

### Git Workflow
- **Commit messages**: **Conventional Commits** (required).
  - Examples: `feat(tracking): add recurring events`, `fix(api): handle empty query`, `chore: bump deps`
- **Branching**: short-lived branches + PRs; default to a trunk-based flow.
  - **Branch naming**: `feat/<change-id>` (and `fix/<change-id>` when appropriate)
- **Merging**: squash-merge PRs.
- **PR expectations**:
  - Keep PRs small and reviewable
  - Link to OpenSpec change ID when applicable
  - Include migration/rollback notes for schema changes

### AI/Assistant Operating Conventions (project-specific)
- Prefer **absolute paths** in tool calls where possible.
- If a request sounds like a proposal/plan/architecture shift, **start by reading** `openspec/AGENTS.md` and use the OpenSpec workflow.
- Don’t invent stack details—leave explicit TODOs if unknown.

## Domain Context (fill this in early; it pays off)
Tracker is intentionally domain-agnostic: everything is modeled as a **counter**.

Key domain questions (to lock in before building APIs):
- **Cardinality/ownership**: **global counters** (single user).
- **Value type**: **integer**.
- **Time/history**: **event log required** — every counter change MUST be stored with a **timestamp** to enable a timeline view later.
- **Naming**: uniqueness constraints (global unique name? namespaced?)

User model / auth:
- **Single-user** system; no authentication required initially.

## Important Constraints
Known constraints:
- **Run locally first** (developer machine) without requiring a hosted environment.
- **Must be Docker-ready** (Docker and/or docker-compose) for later hosting.
- **Keep frontend lightweight** until core counter model stabilizes.

To clarify later:
- PII / compliance constraints: **none** (no PII logic required)
- Expected scale: **single user**, likely **tens/hundreds of counters**, and a **handful of updates per day** (optimize for simplicity over throughput).

## External Dependencies
- **Postgres** (primary datastore)
- **Docker** (runtime packaging/deployment)

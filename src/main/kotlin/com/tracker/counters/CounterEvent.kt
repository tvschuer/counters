package com.tracker.counters

import java.time.Instant
import java.util.UUID

data class CounterEvent(
	val id: UUID,
	val counterId: UUID,
	val delta: Long,
	val occurredAt: Instant,
	val createdAt: Instant,
)


package com.tracker.counters.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "counter_events")
class CounterEventEntity(
	@Id
	@Column(name = "id", nullable = false)
	val id: UUID,

	@Column(name = "counter_id", nullable = false)
	val counterId: UUID,

	@Column(name = "delta", nullable = false)
	val delta: Long,

	@Column(name = "occurred_at", nullable = false)
	val occurredAt: Instant,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant,
) {
	@Suppress("unused")
	protected constructor() : this(
		id = UUID.randomUUID(),
		counterId = UUID.randomUUID(),
		delta = 0L,
		occurredAt = Instant.EPOCH,
		createdAt = Instant.EPOCH,
	)
}


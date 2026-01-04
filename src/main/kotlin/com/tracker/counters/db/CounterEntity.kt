package com.tracker.counters.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "counters")
class CounterEntity(
	@Id
	@Column(name = "id", nullable = false)
	val id: UUID,

	@Column(name = "name", nullable = false)
	var name: String,

	@Column(name = "unit", nullable = false)
	var unit: String,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant,
) {
	@Suppress("unused")
	protected constructor() : this(
		id = UUID.randomUUID(),
		name = "",
		unit = "",
		createdAt = Instant.EPOCH,
	)
}



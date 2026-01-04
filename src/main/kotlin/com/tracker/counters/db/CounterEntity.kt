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

	@Column(name = "value", nullable = false)
	var value: Long,

	@Column(name = "default_amount", nullable = false)
	var defaultAmount: Int,

	@Column(name = "created_at", nullable = false)
	val createdAt: Instant,

	@Column(name = "deleted_at", nullable = true)
	var deletedAt: Instant? = null,
) {
	@Suppress("unused")
	protected constructor() : this(
		id = UUID.randomUUID(),
		name = "",
		unit = "",
		value = 0L,
		defaultAmount = 1,
		createdAt = Instant.EPOCH,
		deletedAt = null,
	)
}



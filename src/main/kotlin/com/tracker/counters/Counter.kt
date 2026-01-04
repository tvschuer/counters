package com.tracker.counters

import java.time.Instant
import java.util.UUID

data class Counter(
	val id: UUID,
	val name: String,
	val unit: String,
	val value: Long,
	val defaultAmount: Int,
	val createdAt: Instant,
	val deletedAt: Instant?,
)



package com.tracker.counters

import java.time.Instant
import java.util.UUID

data class Counter(
	val id: UUID,
	val name: String,
	val unit: String,
	val createdAt: Instant,
)



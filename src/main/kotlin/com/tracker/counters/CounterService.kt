package com.tracker.counters

import com.tracker.counters.db.CounterEntity
import com.tracker.counters.db.CounterRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class CounterService(
	private val repo: CounterRepository,
	private val clock: Clock = Clock.systemUTC(),
) {
	fun createCounter(name: String, unit: String): Counter {
		val trimmedName = name.trim()
		val trimmedUnit = unit.trim()

		require(trimmedName.isNotBlank()) { "name must not be blank" }
		require(trimmedUnit.isNotBlank()) { "unit must not be blank" }

		if (repo.existsByNameIgnoreCase(trimmedName)) {
			throw CounterNameAlreadyExistsException("counter name must be unique")
		}

		val now: Instant = Instant.now(clock)
		val entity = CounterEntity(
			id = UUID.randomUUID(),
			name = trimmedName,
			unit = trimmedUnit,
			createdAt = now,
		)

		return try {
			repo.save(entity).toDomain()
		} catch (e: DataIntegrityViolationException) {
			// Covers race conditions against the DB uniqueness constraint.
			throw CounterNameAlreadyExistsException("counter name must be unique")
		}
	}

	fun listCounters(): List<Counter> = repo.findAllByOrderByNameAsc().map { it.toDomain() }
}

private fun CounterEntity.toDomain(): Counter =
	Counter(
		id = this.id,
		name = this.name,
		unit = this.unit,
		createdAt = this.createdAt,
	)



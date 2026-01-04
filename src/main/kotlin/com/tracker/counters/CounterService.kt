package com.tracker.counters

import com.tracker.counters.db.CounterEntity
import com.tracker.counters.db.CounterEventEntity
import com.tracker.counters.db.CounterEventRepository
import com.tracker.counters.db.CounterRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class CounterService(
	private val repo: CounterRepository,
	private val eventRepo: CounterEventRepository,
	private val clock: Clock = Clock.systemUTC(),
) {
	fun createCounter(name: String, unit: String, defaultAmount: Int? = null): Counter {
		val trimmedName = name.trim()
		val trimmedUnit = unit.trim()
		val actualDefaultAmount = defaultAmount ?: 1

		require(trimmedName.isNotBlank()) { "name must not be blank" }
		require(trimmedUnit.isNotBlank()) { "unit must not be blank" }
		require(actualDefaultAmount > 0) { "default amount must be positive" }

		if (repo.existsByNameIgnoreCase(trimmedName)) {
			throw CounterNameAlreadyExistsException("counter name must be unique")
		}

		val now: Instant = Instant.now(clock)
		val entity = CounterEntity(
			id = UUID.randomUUID(),
			name = trimmedName,
			unit = trimmedUnit,
			value = 0L,
			defaultAmount = actualDefaultAmount,
			createdAt = now,
		)

		return try {
			repo.save(entity).toDomain()
		} catch (e: DataIntegrityViolationException) {
			// Covers race conditions against the DB uniqueness constraint.
			throw CounterNameAlreadyExistsException("counter name must be unique")
		}
	}

	fun listCounters(): List<Counter> = repo.findAllByDeletedAtNullOrderByNameAsc().map { it.toDomain() }

	fun getCounter(counterId: UUID): Counter {
		val counter = repo.findById(counterId).orElseThrow {
			IllegalArgumentException("counter not found")
		}

		if (counter.deletedAt != null) {
			throw IllegalArgumentException("counter is deleted")
		}

		return counter.toDomain()
	}

	@Transactional
	fun updateCounter(counterId: UUID, name: String, unit: String, defaultAmount: Int): Counter {
		val counter = repo.findById(counterId).orElseThrow {
			IllegalArgumentException("counter not found")
		}

		if (counter.deletedAt != null) {
			throw IllegalArgumentException("counter is deleted")
		}

		val trimmedName = name.trim()
		val trimmedUnit = unit.trim()

		require(trimmedName.isNotBlank()) { "name must not be blank" }
		require(trimmedUnit.isNotBlank()) { "unit must not be blank" }
		require(defaultAmount > 0) { "default amount must be positive" }

		// Check for name uniqueness (excluding current counter)
		if (trimmedName.lowercase() != counter.name.lowercase() && 
		    repo.existsByNameIgnoreCase(trimmedName)) {
			throw CounterNameAlreadyExistsException("counter name must be unique")
		}

		counter.name = trimmedName
		counter.unit = trimmedUnit
		counter.defaultAmount = defaultAmount

		return try {
			repo.save(counter).toDomain()
		} catch (e: DataIntegrityViolationException) {
			throw CounterNameAlreadyExistsException("counter name must be unique")
		}
	}

	@Transactional
	fun deleteCounter(counterId: UUID) {
		val counter = repo.findById(counterId).orElseThrow {
			IllegalArgumentException("counter not found")
		}

		if (counter.deletedAt != null) {
			throw IllegalArgumentException("counter is already deleted")
		}

		counter.deletedAt = Instant.now(clock)
		repo.save(counter)
	}

	@Transactional
	fun increment(counterId: UUID, amount: Int? = null, occurredAt: Instant? = null): Counter {
		val counter = repo.findById(counterId).orElseThrow {
			IllegalArgumentException("counter not found")
		}

		if (counter.deletedAt != null) {
			throw IllegalArgumentException("counter is deleted")
		}

		val actualAmount = (amount ?: counter.defaultAmount).toLong()
		if (actualAmount == 0L) {
			// No-op: return current state without creating an event
			return counter.toDomain()
		}

		return applyDelta(counter, actualAmount, occurredAt)
	}

	@Transactional
	fun decrement(counterId: UUID, amount: Int? = null, occurredAt: Instant? = null): Counter {
		val counter = repo.findById(counterId).orElseThrow {
			IllegalArgumentException("counter not found")
		}

		if (counter.deletedAt != null) {
			throw IllegalArgumentException("counter is deleted")
		}

		val actualAmount = (amount ?: counter.defaultAmount).toLong()
		if (actualAmount == 0L) {
			// No-op: return current state without creating an event
			return counter.toDomain()
		}

		return applyDelta(counter, -actualAmount, occurredAt)
	}

	private fun applyDelta(counter: CounterEntity, delta: Long, occurredAt: Instant?): Counter {
		val now = Instant.now(clock)
		val eventOccurredAt = occurredAt ?: now

		// Update counter value
		counter.value += delta

		// Persist event
		val event = CounterEventEntity(
			id = UUID.randomUUID(),
			counterId = counter.id,
			delta = delta,
			occurredAt = eventOccurredAt,
			createdAt = now,
		)
		eventRepo.save(event)

		return repo.save(counter).toDomain()
	}
}

private fun CounterEntity.toDomain(): Counter =
	Counter(
		id = this.id,
		name = this.name,
		unit = this.unit,
		value = this.value,
		defaultAmount = this.defaultAmount,
		createdAt = this.createdAt,
		deletedAt = this.deletedAt,
	)



package com.tracker.counters

import com.tracker.counters.db.CounterEntity
import com.tracker.counters.db.CounterEventEntity
import com.tracker.counters.db.CounterEventRepository
import com.tracker.counters.db.CounterRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

class CounterServiceTest {
	@Test
	fun `createCounter returns UUID and trims inputs`() {
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		`when`(repo.existsByNameIgnoreCase("Water")).thenReturn(false)
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val fixedClock = Clock.fixed(Instant.parse("2026-01-04T12:00:00Z"), ZoneOffset.UTC)
		val service = CounterService(repo, eventRepo, fixedClock)

		val counter = service.createCounter("  Water  ", "  glasses  ")

		assertEquals("Water", counter.name)
		assertEquals("glasses", counter.unit)
		assertEquals(0L, counter.value)
		assertEquals(1, counter.defaultAmount)
		assertEquals(Instant.parse("2026-01-04T12:00:00Z"), counter.createdAt)
	}

	@Test
	fun `createCounter accepts custom defaultAmount`() {
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		`when`(repo.existsByNameIgnoreCase("Water")).thenReturn(false)
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val service = CounterService(repo, eventRepo)

		val counter = service.createCounter("Water", "glasses", defaultAmount = 5)

		assertEquals(5, counter.defaultAmount)
	}

	@Test
	fun `createCounter rejects duplicate name`() {
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		`when`(repo.existsByNameIgnoreCase("Water")).thenReturn(true)

		val service = CounterService(repo, eventRepo)

		assertThrows(CounterNameAlreadyExistsException::class.java) {
			service.createCounter("Water", "glasses")
		}
	}

	@Test
	fun `increment uses default amount when amount is null`() {
		val id = UUID.randomUUID()
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		val entity = CounterEntity(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 10L,
			defaultAmount = 3,
			createdAt = Instant.EPOCH,
		)
		`when`(repo.findById(id)).thenReturn(java.util.Optional.of(entity))
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }
		`when`(eventRepo.save(any(CounterEventEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val fixedClock = Clock.fixed(Instant.parse("2026-01-04T12:00:00Z"), ZoneOffset.UTC)
		val service = CounterService(repo, eventRepo, fixedClock)

		val counter = service.increment(id, amount = null)

		assertEquals(13L, counter.value)
		verify(eventRepo).save(any(CounterEventEntity::class.java))
	}

	@Test
	fun `increment uses provided amount when specified`() {
		val id = UUID.randomUUID()
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		val entity = CounterEntity(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 10L,
			defaultAmount = 3,
			createdAt = Instant.EPOCH,
		)
		`when`(repo.findById(id)).thenReturn(java.util.Optional.of(entity))
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }
		`when`(eventRepo.save(any(CounterEventEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val service = CounterService(repo, eventRepo)

		val counter = service.increment(id, amount = 7)

		assertEquals(17L, counter.value)
	}

	@Test
	fun `decrement uses default amount when amount is null`() {
		val id = UUID.randomUUID()
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		val entity = CounterEntity(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 10L,
			defaultAmount = 2,
			createdAt = Instant.EPOCH,
		)
		`when`(repo.findById(id)).thenReturn(java.util.Optional.of(entity))
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }
		`when`(eventRepo.save(any(CounterEventEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val service = CounterService(repo, eventRepo)

		val counter = service.decrement(id, amount = null)

		assertEquals(8L, counter.value)
	}

	@Test
	fun `increment with amount=0 is a no-op`() {
		val id = UUID.randomUUID()
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		val entity = CounterEntity(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 10L,
			defaultAmount = 1,
			createdAt = Instant.EPOCH,
		)
		`when`(repo.findById(id)).thenReturn(java.util.Optional.of(entity))

		val service = CounterService(repo, eventRepo)

		val counter = service.increment(id, amount = 0)

		assertEquals(10L, counter.value)
		Mockito.verifyNoInteractions(eventRepo)
	}

	@Test
	fun `increment uses caller-provided occurredAt when specified`() {
		val id = UUID.randomUUID()
		val repo = Mockito.mock(CounterRepository::class.java)
		val eventRepo = Mockito.mock(CounterEventRepository::class.java)
		val entity = CounterEntity(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 10L,
			defaultAmount = 1,
			createdAt = Instant.EPOCH,
		)
		`when`(repo.findById(id)).thenReturn(java.util.Optional.of(entity))
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }
		`when`(eventRepo.save(any(CounterEventEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val customTime = Instant.parse("2025-12-25T10:00:00Z")
		val service = CounterService(repo, eventRepo)

		service.increment(id, amount = 1, occurredAt = customTime)

		verify(eventRepo).save(any(CounterEventEntity::class.java))
	}
}



package com.tracker.counters

import com.tracker.counters.db.CounterEntity
import com.tracker.counters.db.CounterRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class CounterServiceTest {
	@Test
	fun `createCounter returns UUID and trims inputs`() {
		val repo = Mockito.mock(CounterRepository::class.java)
		`when`(repo.existsByNameIgnoreCase("Water")).thenReturn(false)
		`when`(repo.save(any(CounterEntity::class.java))).thenAnswer { inv -> inv.getArgument(0) }

		val fixedClock = Clock.fixed(Instant.parse("2026-01-04T12:00:00Z"), ZoneOffset.UTC)
		val service = CounterService(repo, fixedClock)

		val counter = service.createCounter("  Water  ", "  glasses  ")

		assertEquals("Water", counter.name)
		assertEquals("glasses", counter.unit)
		assertEquals(Instant.parse("2026-01-04T12:00:00Z"), counter.createdAt)
	}

	@Test
	fun `createCounter rejects duplicate name`() {
		val repo = Mockito.mock(CounterRepository::class.java)
		`when`(repo.existsByNameIgnoreCase("Water")).thenReturn(true)

		val service = CounterService(repo)

		assertThrows(CounterNameAlreadyExistsException::class.java) {
			service.createCounter("Water", "glasses")
		}
	}
}



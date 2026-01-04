package com.tracker.counters.api

import com.tracker.counters.Counter
import com.tracker.counters.CounterNameAlreadyExistsException
import com.tracker.counters.CounterService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.UUID

class CounterApiControllerTest {
	@Test
	fun `create returns 201 and Location header`() {
		val service = mock(CounterService::class.java)
		val controller = CounterApiController(service)

		val id = UUID.fromString("c7fdce52-70ff-45f3-b808-5d80c08b9e11")
		`when`(service.createCounter("Water", "glasses", null)).thenReturn(
			Counter(
				id = id,
				name = "Water",
				unit = "glasses",
				value = 0L,
				defaultAmount = 1,
				createdAt = Instant.EPOCH,
			),
		)

		val res = controller.create(CreateCounterRequest(name = "Water", unit = "glasses"))
		assert(res.statusCode == HttpStatus.CREATED)
		assert(res.headers.location?.toString() == "/counters/$id")
		assert(res.body?.id == id)
	}

	@Test
	fun `create throws on blank name (service-level validation)`() {
		val service = CounterService(
			repo = mock(com.tracker.counters.db.CounterRepository::class.java),
			eventRepo = mock(com.tracker.counters.db.CounterEventRepository::class.java),
		)
		assertThrows<IllegalArgumentException> {
			service.createCounter("   ", "glasses")
		}
	}

	@Test
	fun `increment returns updated counter`() {
		val service = mock(CounterService::class.java)
		val controller = CounterApiController(service)

		val id = UUID.randomUUID()
		val updatedCounter = Counter(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 5L,
			defaultAmount = 1,
			createdAt = Instant.EPOCH,
		)
		`when`(service.increment(id, null, null)).thenReturn(updatedCounter)

		val res = controller.increment(id, UpdateCounterRequest())

		assertEquals(HttpStatus.OK, res.statusCode)
		assertEquals(5L, res.body?.value)
		assertEquals(id, res.body?.id)
	}

	@Test
	fun `decrement returns updated counter`() {
		val service = mock(CounterService::class.java)
		val controller = CounterApiController(service)

		val id = UUID.randomUUID()
		val updatedCounter = Counter(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 3L,
			defaultAmount = 1,
			createdAt = Instant.EPOCH,
		)
		`when`(service.decrement(id, null, null)).thenReturn(updatedCounter)

		val res = controller.decrement(id, UpdateCounterRequest())

		assertEquals(HttpStatus.OK, res.statusCode)
		assertEquals(3L, res.body?.value)
		assertEquals(id, res.body?.id)
	}

	@Test
	fun `increment with custom amount passes amount to service`() {
		val service = mock(CounterService::class.java)
		val controller = CounterApiController(service)

		val id = UUID.randomUUID()
		val customTime = Instant.parse("2025-12-01T10:00:00Z")
		val updatedCounter = Counter(
			id = id,
			name = "Water",
			unit = "glasses",
			value = 10L,
			defaultAmount = 1,
			createdAt = Instant.EPOCH,
		)
		`when`(service.increment(id, 5, customTime)).thenReturn(updatedCounter)

		val res = controller.increment(id, UpdateCounterRequest(amount = 5, occurredAt = customTime))

		assertEquals(HttpStatus.OK, res.statusCode)
		assertEquals(10L, res.body?.value)
	}
}



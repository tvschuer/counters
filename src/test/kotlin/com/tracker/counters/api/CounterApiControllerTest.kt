package com.tracker.counters.api

import com.tracker.counters.Counter
import com.tracker.counters.CounterNameAlreadyExistsException
import com.tracker.counters.CounterService
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
		`when`(service.createCounter("Water", "glasses")).thenReturn(
			Counter(id = id, name = "Water", unit = "glasses", createdAt = Instant.EPOCH),
		)

		val res = controller.create(CreateCounterRequest(name = "Water", unit = "glasses"))
		assert(res.statusCode == HttpStatus.CREATED)
		assert(res.headers.location?.toString() == "/counters/$id")
		assert(res.body?.id == id)
	}

	@Test
	fun `create throws on blank name (service-level validation)`() {
		val service = CounterService(repo = mock(com.tracker.counters.db.CounterRepository::class.java))
		assertThrows<IllegalArgumentException> {
			service.createCounter("   ", "glasses")
		}
	}
}



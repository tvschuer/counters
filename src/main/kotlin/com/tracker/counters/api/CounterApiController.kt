package com.tracker.counters.api

import com.tracker.counters.CounterNameAlreadyExistsException
import com.tracker.counters.CounterService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/counters")
@Validated
class CounterApiController(
	private val service: CounterService,
) {
	@PostMapping
	fun create(@Valid @RequestBody req: CreateCounterRequest): ResponseEntity<CreateCounterResponse> {
		val counter = service.createCounter(req.name, req.unit, req.defaultAmount)
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.header("Location", "/counters/${counter.id}")
			.body(CreateCounterResponse(id = counter.id))
	}

	@PostMapping("/{id}/increment")
	fun increment(
		@PathVariable id: UUID,
		@Valid @RequestBody req: UpdateCounterRequest,
	): ResponseEntity<CounterResponse> {
		val counter = service.increment(id, req.amount, req.occurredAt)
		return ResponseEntity.ok(CounterResponse.from(counter))
	}

	@PostMapping("/{id}/decrement")
	fun decrement(
		@PathVariable id: UUID,
		@Valid @RequestBody req: UpdateCounterRequest,
	): ResponseEntity<CounterResponse> {
		val counter = service.decrement(id, req.amount, req.occurredAt)
		return ResponseEntity.ok(CounterResponse.from(counter))
	}

	@ExceptionHandler(CounterNameAlreadyExistsException::class)
	fun duplicateName(e: CounterNameAlreadyExistsException): ResponseEntity<ApiError> =
		ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiError(message = e.message ?: "counter name must be unique"))

	@ExceptionHandler(IllegalArgumentException::class)
	fun illegalArg(e: IllegalArgumentException): ResponseEntity<ApiError> =
		ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiError(message = e.message ?: "validation error"))

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun invalid(e: MethodArgumentNotValidException): ResponseEntity<ApiError> =
		ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiError(message = "validation error"))
}

data class CreateCounterRequest(
	@field:NotBlank val name: String,
	@field:NotBlank val unit: String,
	@field:Positive val defaultAmount: Int? = null,
)

data class CreateCounterResponse(
	val id: UUID,
)

data class UpdateCounterRequest(
	val amount: Int? = null,
	val occurredAt: Instant? = null,
)

data class CounterResponse(
	val id: UUID,
	val name: String,
	val unit: String,
	val value: Long,
	val defaultAmount: Int,
) {
	companion object {
		fun from(counter: com.tracker.counters.Counter) = CounterResponse(
			id = counter.id,
			name = counter.name,
			unit = counter.unit,
			value = counter.value,
			defaultAmount = counter.defaultAmount,
		)
	}
}

data class ApiError(
	val message: String,
)



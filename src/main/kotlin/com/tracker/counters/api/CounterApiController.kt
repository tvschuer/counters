package com.tracker.counters.api

import com.tracker.counters.CounterNameAlreadyExistsException
import com.tracker.counters.CounterService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/counters")
@Validated
class CounterApiController(
	private val service: CounterService,
) {
	@PostMapping
	fun create(@Valid @RequestBody req: CreateCounterRequest): ResponseEntity<CreateCounterResponse> {
		val counter = service.createCounter(req.name, req.unit)
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.header("Location", "/counters/${counter.id}")
			.body(CreateCounterResponse(id = counter.id))
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
)

data class CreateCounterResponse(
	val id: UUID,
)

data class ApiError(
	val message: String,
)



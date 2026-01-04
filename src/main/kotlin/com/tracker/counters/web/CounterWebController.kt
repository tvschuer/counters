package com.tracker.counters.web

import com.tracker.counters.CounterNameAlreadyExistsException
import com.tracker.counters.CounterService
import jakarta.validation.constraints.NotBlank
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class RootController {
	@GetMapping("/")
	fun root(): String = "redirect:/counters"
}

@Controller
@RequestMapping("/counters")
class CounterWebController(
	private val service: CounterService,
) {
	@GetMapping
	fun list(model: Model): String {
		model.addAttribute("counters", service.listCounters())
		return "counters/list"
	}

	@GetMapping("/new")
	fun newCounter(model: Model): String {
		model.addAttribute("form", CreateCounterForm())
		return "counters/new"
	}

	@PostMapping
	fun create(
		@Validated @ModelAttribute("form") form: CreateCounterForm,
		binding: BindingResult,
		model: Model,
	): String {
		if (binding.hasErrors()) return "counters/new"

		return try {
			service.createCounter(form.name, form.unit)
			"redirect:/counters"
		} catch (e: CounterNameAlreadyExistsException) {
			model.addAttribute("errorMessage", e.message ?: "counter name must be unique")
			"counters/new"
		} catch (e: IllegalArgumentException) {
			model.addAttribute("errorMessage", e.message ?: "validation error")
			"counters/new"
		}
	}
}

data class CreateCounterForm(
	@field:NotBlank var name: String = "",
	@field:NotBlank var unit: String = "",
)



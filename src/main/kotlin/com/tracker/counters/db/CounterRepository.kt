package com.tracker.counters.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CounterRepository : JpaRepository<CounterEntity, UUID> {
	fun existsByNameIgnoreCase(name: String): Boolean
	fun findAllByOrderByNameAsc(): List<CounterEntity>
}



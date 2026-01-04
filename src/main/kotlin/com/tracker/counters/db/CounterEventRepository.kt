package com.tracker.counters.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CounterEventRepository : JpaRepository<CounterEventEntity, UUID>


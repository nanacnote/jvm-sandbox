package com.hlabs.rbme.repository

import com.hlabs.rbme.domain.entity.RuleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RuleRepository : JpaRepository<RuleEntity, Long> {
    fun findByMapTo(value: String): Optional<RuleEntity>
}
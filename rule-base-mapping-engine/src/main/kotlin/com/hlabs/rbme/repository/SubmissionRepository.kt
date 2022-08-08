package com.hlabs.rbme.repository

import com.hlabs.rbme.domain.entity.SubmissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SubmissionRepository : JpaRepository<SubmissionEntity, Long> {
    fun findByComplete(value: Boolean): Optional<SubmissionEntity>
}
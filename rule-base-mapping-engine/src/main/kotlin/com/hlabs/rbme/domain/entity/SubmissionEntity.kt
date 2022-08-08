package com.hlabs.rbme.domain.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob

@Entity
data class SubmissionEntity(
    @Id
    @GeneratedValue
    val id: Long,
    var complete: Boolean,
    @Lob
    var configuration: String? = null,
)

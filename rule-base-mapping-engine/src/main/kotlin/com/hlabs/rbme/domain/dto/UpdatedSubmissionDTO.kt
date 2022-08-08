package com.hlabs.rbme.domain.dto

import com.hlabs.rbme.domain.data.Instruction

data class UpdatedSubmissionDTO(
    val id: Long,
    val instructions: Set<Instruction>,
    val updateRule: Boolean = false,
    var match: SubmissionDTO? = null,
)
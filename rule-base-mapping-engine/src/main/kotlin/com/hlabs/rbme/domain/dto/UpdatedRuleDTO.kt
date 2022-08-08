package com.hlabs.rbme.domain.dto

data class UpdatedRuleDTO(
    val id: Long,
    val fieldNames: Set<String>,
)

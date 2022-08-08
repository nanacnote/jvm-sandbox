package com.hlabs.rbme.domain.dto

data class RuleDTO(
    var id: Long? = null,
    var mapTo: String? = null,
    var fieldNames: Set<String>? = null
)


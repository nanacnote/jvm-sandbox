package com.hlabs.rbme.domain.data

data class Feedback(
    val fieldName: String,
    val mapTo: String?,
    val isResolved: Boolean,
    val isRejected: Boolean,
    val suggestions: Set<String>
)
package com.hlabs.rbme.domain.dto


data class InitialSubmissionDTO(
    val ruleID: Long? = null,
    val fileType: String,
    val fileAsBase64: String,
)

package com.hlabs.rbme.domain.dto

import com.hlabs.rbme.domain.data.Feedback
import com.fasterxml.jackson.databind.ObjectMapper

data class SubmissionDTO(
    var id: Long? = null,
    var complete: Boolean? = null,
    var configuration: String? = null,
    var fieldNames: Set<String>? = null,
    var resolvedFieldNames: Set<String>? = null,
    var rejectedFieldNames: Set<String>? = null,
    var feedbackFieldNames: Set<Feedback>? = null,
) {


    fun serializeConfiguration(jacksonMapper: ObjectMapper): SubmissionDTO {
        this.configuration = jacksonMapper.writeValueAsString(this)
        return this
    }

    fun deserializeConfiguration(jacksonMapper: ObjectMapper): SubmissionDTO {
        val deserializedObject = jacksonMapper.readValue(this.configuration, SubmissionDTO::class.java)
        deserializedObject.id = this.id
        return deserializedObject
    }
}

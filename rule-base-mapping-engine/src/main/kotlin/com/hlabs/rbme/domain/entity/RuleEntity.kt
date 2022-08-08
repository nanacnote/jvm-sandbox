package com.hlabs.rbme.domain.entity

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class RuleEntity(
    @Id
    @GeneratedValue
    val id: Long,
    var mapTo: String,
    @ElementCollection
    var fieldNames: Set<String>,
)


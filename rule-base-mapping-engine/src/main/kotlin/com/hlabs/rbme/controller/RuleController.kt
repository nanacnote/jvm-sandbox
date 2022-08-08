package com.hlabs.rbme.controller

import com.hlabs.rbme.domain.dto.InitialRuleDTO
import com.hlabs.rbme.domain.dto.RuleDTO
import com.hlabs.rbme.domain.dto.UpdatedRuleDTO
import com.hlabs.rbme.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rule")
class RuleController @Autowired constructor(
    val service: RuleService
) {

    @GetMapping
    fun fetchAllEntries(): List<RuleDTO> {
        return service.getAll()
    }

    @GetMapping("/{id}")
    fun fetchEntryById(@PathVariable(value = "id") id: Long): RuleDTO {
        return service.get(id)
    }

    @PostMapping
    fun addEntry(@RequestBody rule: InitialRuleDTO): RuleDTO {
        return service.add(rule)
    }

    @PutMapping
    fun updateEntry(@RequestBody rule: UpdatedRuleDTO): RuleDTO {
        return service.update(rule)
    }

    @DeleteMapping("/{id}")
    fun removeEntry(@PathVariable(value = "id") id: Long): String {
        return service.remove(id)
    }
}
package com.hlabs.rbme.controller

import com.hlabs.rbme.domain.dto.InitialSubmissionDTO
import com.hlabs.rbme.domain.dto.SubmissionDTO
import com.hlabs.rbme.domain.dto.UpdatedSubmissionDTO
import com.hlabs.rbme.service.SubmissionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/submission")
class SubmissionController @Autowired constructor(
    val service: SubmissionService
) {

    @GetMapping
    fun fetchAllEntries(): List<SubmissionDTO> {
        return service.getAll()
    }

    @GetMapping("/{id}")
    fun fetchEntryById(@PathVariable(value = "id") id: Long): SubmissionDTO {
        return service.get(id)
    }

    @PostMapping
    fun addEntry(@RequestBody data: InitialSubmissionDTO): SubmissionDTO {
        return service.add(data)
    }

    @PutMapping
    fun updateEntry(@RequestBody data: UpdatedSubmissionDTO): SubmissionDTO {
        return service.update(data)
    }

    @DeleteMapping("/{id}")
    fun removeEntry(@PathVariable(value = "id") id: Long): String {
        return service.remove(id)
    }
}
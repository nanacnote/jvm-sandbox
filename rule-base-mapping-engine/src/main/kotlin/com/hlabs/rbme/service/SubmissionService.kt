package com.hlabs.rbme.service

import com.hlabs.rbme.component.Translator
import com.hlabs.rbme.domain.dto.InitialSubmissionDTO
import com.hlabs.rbme.domain.dto.SubmissionDTO
import com.hlabs.rbme.domain.dto.UpdatedSubmissionDTO
import com.hlabs.rbme.domain.entity.SubmissionEntity
import com.hlabs.rbme.exception.NotFoundException
import com.hlabs.rbme.repository.SubmissionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class SubmissionService @Autowired constructor(
    val repo: SubmissionRepository,
    val ruleService: RuleService,
    val modelMapper: ModelMapper,
    val jacksonMapper: ObjectMapper,
    val translator: Translator
) {

    fun getAll(): List<SubmissionDTO> {
        return repo.findAll()
            .map { modelMapper.map(it, SubmissionDTO::class.java).deserializeConfiguration(jacksonMapper) }
    }

    fun get(id: Long): SubmissionDTO {
        val entry: Optional<SubmissionEntity> = repo.findById(id).map { it }
        return if (entry.isPresent) {
            val dto = modelMapper.map(entry.get(), SubmissionDTO::class.java)
            dto.deserializeConfiguration(jacksonMapper)
        } else {
            throw NotFoundException()
        }
    }

    fun add(data: InitialSubmissionDTO): SubmissionDTO {
        val dto = translator.translate(data)
        val savedEntry =
            repo.save(modelMapper.map(dto.serializeConfiguration(jacksonMapper), SubmissionEntity::class.java))
        dto.id = savedEntry.id
        return dto
    }

    fun update(data: UpdatedSubmissionDTO): SubmissionDTO {
        val entry: Optional<SubmissionEntity> = repo.findById(data.id).map { it }
        return if (entry.isPresent) {
            if (data.updateRule) ruleService.updateViaSubmission(data.instructions)
            val existingEntry = entry.get()
            data.match =
                modelMapper.map(existingEntry, SubmissionDTO::class.java).deserializeConfiguration(jacksonMapper)
            val dto = translator.translate(data)
            existingEntry.configuration =
                modelMapper.map(dto.serializeConfiguration(jacksonMapper), SubmissionEntity::class.java).configuration
            repo.save(existingEntry)
            dto.id = existingEntry.id
            dto
        } else {
            throw NotFoundException()
        }
    }

    fun remove(id: Long): String {
        val entry: Optional<SubmissionEntity> = repo.findById(id).map { it }
        if (entry.isPresent) {
            repo.deleteById(id)
            return "OK"
        } else {
            throw NotFoundException()
        }
    }

}
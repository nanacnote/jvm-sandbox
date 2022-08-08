package com.hlabs.rbme.service

import com.hlabs.rbme.component.Translator
import com.hlabs.rbme.domain.data.Instruction
import com.hlabs.rbme.domain.dto.InitialRuleDTO
import com.hlabs.rbme.domain.dto.RuleDTO
import com.hlabs.rbme.domain.dto.UpdatedRuleDTO
import com.hlabs.rbme.domain.entity.RuleEntity
import com.hlabs.rbme.exception.AlreadyExistException
import com.hlabs.rbme.exception.NotFoundException
import com.hlabs.rbme.repository.RuleRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RuleService @Autowired constructor(
    val repo: RuleRepository,
    val modelMapper: ModelMapper,
    val translator: Translator
) {

    fun getAll(): List<RuleDTO> {
        return repo.findAll().map { modelMapper.map(it, RuleDTO::class.java) }
    }

    fun get(id: Long): RuleDTO {
        val entry: Optional<RuleEntity> = repo.findById(id).map { it }
        return if (entry.isPresent) {
            modelMapper.map(entry.get(), RuleDTO::class.java)
        } else {
            throw NotFoundException()
        }
    }

    fun add(rule: InitialRuleDTO): RuleDTO {
        val entry: Optional<RuleEntity> = repo.findByMapTo(rule.mapTo).map { it }
        return if (entry.isPresent) {
            throw AlreadyExistException()
        } else {
            val newRule = repo.save(modelMapper.map(rule, RuleEntity::class.java))
            translator.expandVocabulary()
            modelMapper.map(newRule, RuleDTO::class.java)
        }
    }

    fun update(rule: UpdatedRuleDTO): RuleDTO {
        val entry: Optional<RuleEntity> = repo.findById(rule.id).map { it }
        return if (entry.isPresent) {
            val matchingRule = entry.get()
            matchingRule.fieldNames = matchingRule.fieldNames.plus(rule.fieldNames).toSet()
            val updatedRule = repo.save(matchingRule)
            translator.expandVocabulary()
            modelMapper.map(updatedRule, RuleDTO::class.java)
        } else {
            throw NotFoundException()
        }
    }

    fun updateViaSubmission(rules: Set<Instruction>) {
        for (rule in rules) {
            val entry: Optional<RuleEntity> = repo.findByMapTo(rule.mapTo).map { it }
            if (entry.isPresent) {
                val matchingRule = entry.get()
                matchingRule.fieldNames = matchingRule.fieldNames.plus(setOf(rule.fieldName))
                repo.save(matchingRule)
            } else {
                repo.save(
                    modelMapper.map(
                        InitialRuleDTO(mapTo = rule.mapTo, fieldNames = setOf(rule.fieldName)),
                        RuleEntity::class.java
                    )
                )
            }
        }
        translator.expandVocabulary()
    }

    fun remove(id: Long): String {
        val rule: Optional<RuleEntity> = repo.findById(id).map { it }
        if (rule.isPresent) {
            repo.deleteById(id)
            translator.expandVocabulary()
            return "OK"
        } else {
            throw NotFoundException()
        }
    }
}
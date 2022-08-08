package com.hlabs.rbme.component

import com.hlabs.rbme.domain.data.Feedback
import com.hlabs.rbme.domain.dto.InitialSubmissionDTO
import com.hlabs.rbme.domain.dto.SubmissionDTO
import com.hlabs.rbme.domain.dto.UpdatedSubmissionDTO
import com.hlabs.rbme.exception.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class Translator @Autowired constructor(
    private val vocabulary: Vocabulary
) {

    init {
        expandVocabulary()
    }

    final fun expandVocabulary() {
        vocabulary.expand()
    }

    fun translate(data: InitialSubmissionDTO): SubmissionDTO {
        val decodedString = String(Base64.getDecoder().decode(data.fileAsBase64))
        return when (data.fileType) {
            "csv" -> {
                val fieldNames = decodedString.trim().split("\n")[0].split(",").toSet()
                val mappingResults = fieldNames.map { Pair(it, vocabulary.lookup(it)) }
                handleMappingResult(fieldNames, mappingResults)
            }
            else -> throw BadRequestException()
        }
    }

    fun translate(data: UpdatedSubmissionDTO): SubmissionDTO {
        val fieldNames = data.match?.fieldNames ?: emptySet()
        val mappingResults = mutableListOf<Pair<String, Set<String>>>()
        for (fieldName in fieldNames) {
            val match = data.instructions.firstOrNull { it.fieldName == fieldName }
            if((match != null) && match.mapTo.isNotEmpty()){
                mappingResults.add(Pair(fieldName, setOf(match.mapTo)))
            }else{
                mappingResults.add(Pair(fieldName, vocabulary.lookup(fieldName)))
            }
        }
        return handleMappingResult(fieldNames, mappingResults)
    }

    private fun handleMappingResult(
        fieldNames: Set<String>,
        mappingResults: List<Pair<String, Set<String>>>
    ): SubmissionDTO {
        val resolvedFieldNames = mutableSetOf<String>()
        val rejectedFieldNames = mutableSetOf<String>()
        val feedbackFieldNames = mutableSetOf<Feedback>()
        for ((fieldName, suggestions) in mappingResults) {
            if (suggestions.isNotEmpty()) {
                resolvedFieldNames.add(fieldName)
                feedbackFieldNames.add(
                    Feedback(
                        fieldName = fieldName,
                        isResolved = true,
                        isRejected = false,
                        suggestions = suggestions,
                        mapTo = useBest(suggestions)
                    )
                )
            } else {
                rejectedFieldNames.add(fieldName)
                feedbackFieldNames.add(
                    Feedback(
                        fieldName = fieldName,
                        isResolved = false,
                        isRejected = true,
                        suggestions = suggestions,
                        mapTo = usePrediction()
                    )
                )
            }
        }
        return SubmissionDTO(
            complete = rejectedFieldNames.isEmpty(),
            fieldNames = fieldNames,
            resolvedFieldNames = resolvedFieldNames,
            rejectedFieldNames = rejectedFieldNames,
            feedbackFieldNames = feedbackFieldNames
        )
    }

    private fun usePrediction(): String? {
//        TODO
//        implement some predictive algo here
        return null
    }

    private fun useBest(suggestions: Set<String>): String? {
//        TODO
//        introduce mapTo weighting at this point
        return suggestions.firstOrNull()
    }
}
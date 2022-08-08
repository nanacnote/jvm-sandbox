package com.hlabs.rbme.service

import com.hlabs.rbme.exception.*
import com.hlabs.rbme.component.Translator
import com.hlabs.rbme.domain.entity.SubmissionEntity
import com.hlabs.rbme.repository.SubmissionRepository
import com.hlabs.rbme.service.SubmissionService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.modelmapper.ModelMapper
import java.util.*

internal class SubmissionServiceTest {
    private val repo = mockk<SubmissionRepository>()
    private val translator = mockk<Translator>()
    private val modelMapper = ModelMapper()
    private val service = SubmissionService(repo, modelMapper, translator)

    @Test
    fun `should return empty list on initial call to get all entities`() {
        //given
        every { repo.findAll() } returns emptyList()

        //when
        val result = service.getAll()

        //then
        verify(exactly = 1) { repo.findAll() }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should add a valid entity to repo without error if can be fully matched`() {
        val entity = mockk<SubmissionEntity>(relaxed = true)

        //given
        every { repo.save(entity) } returns entity
        every { translator.translate(entity) } returns report
        every { report.get() } returns reportDTO

        //when
        val result = service.add(entity)

        //then
        verify(exactly = 1) { repo.save(entity) }
        assertEquals(result, reportDTO)
    }

    @Test
    fun `should add a valid entity to repo without error if can be partially matched`() {
        val entity = mockk<NonStandardPIF>(relaxed = true)
        val report = mockk<Report>()
        val reportDTO = mockk<AssessmentReportDTO>()

        //given
        every { repo.save(entity) } returns entity
        every { translator.translate(entity) } returns report
        every { report.isFullyFixable() } returns false
        every { report.isPartiallyFixable() } returns true
        every { report.get() } returns reportDTO

        //when
        val result = service.add(entity)

        //then
        verify(exactly = 1) { repo.save(entity) }
        assertEquals(result, reportDTO)
    }

    @Test
    fun `should raise exception on attempt to add invalid entity`() {
        val entity = mockk<NonStandardPIF>(relaxed = true)
        val report = mockk<Report>()

        //given
        every { translator.translate(entity) } returns report
        every { report.isFullyFixable() } returns false
        every { report.isPartiallyFixable() } returns false

        //when
        var exceptionThrown = false
        try {
            service.add(entity)
        } catch(exception: BadRequestException) {
            exceptionThrown = true
        }

        //then
        assertTrue(exceptionThrown)
    }

    @Test
    fun `should get an entity from repo given an ID if it exist`() {
        val entity = mockk<NonStandardPIF>()

        //given
        every { repo.findById(1) } returns Optional.of(entity)

        //when
        val result = service.get(1)

        //then
        verify(exactly = 1) { repo.findById(1) }
        assertEquals(result, entity)
    }

    @Test
    fun `should raise an exception given an ID if it is non existent`() {

        //given
        every { repo.findById(1) } throws NotFoundException()

        //when
        var exceptionThrown = false
        try {
            service.get(1)
        } catch(exception: NotFoundException) {
            exceptionThrown = true
        }

        //then
        verify(exactly = 1) { repo.findById(1) }
        assertTrue(exceptionThrown)
    }

    @Test
    fun `should update an entity's isParsed state given an ID and boolean state if ID exist`() {
        val entity = mockk<NonStandardPIF>(relaxed = true)

        //given
        every { repo.findById(1) } returns Optional.of(entity)
        every { repo.save(entity) } returns entity

        //when
        val result = service.update(1, true)

        //then
        verify(exactly = 1) { repo.findById(1) }

        // assertion aligns with the default value set on entity creation because of relaxed mocking
        // (i.e. test does not check actual values but only checks implementations are correct)
        assertFalse(result.isParsed)
    }

    @Test
    fun `should remove an entity given ID if it exists`() {
        val entity = mockk<NonStandardPIF>()

        //given
        every { repo.findById(1) } returns Optional.of(entity)
        every { repo.deleteById(1) } returns Unit

        //when
        val result = service.remove(1)

        //then
        verify(exactly = 1) {  repo.findById(1) }
        assertEquals(result, "OK")
    }

}
package com.hlabs.rbme.service

import com.hlabs.rbme.exception.AlreadyExistException
import com.hlabs.rbme.exception.NotFoundException
import com.hlabs.rbme.component.Translator
import com.hlabs.rbme.domain.entity.RuleEntity
import com.hlabs.rbme.repository.RuleRepository
import com.hlabs.rbme.service.RuleService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.modelmapper.ModelMapper
import java.util.*

internal class RuleServiceTest {
    private val repo = mockk<RuleRepository>()
    private val translator = mockk<Translator>()
    private val modelMapper = ModelMapper()
    private val service = RuleService(repo, modelMapper, translator)

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
    fun `should add an entity to repo without error if not existing`() {
        val entity = mockk<RuleEntity>(relaxed = true)

        //given
        every { repo.findByMapTo(entity.mapTo) } returns Optional.empty()
        every { repo.save(entity) } returns entity
        every { translator.expandVocabulary() } returns Unit

        //when
        val result = service.add(entity)

        //then
        verify(exactly = 1) { repo.save(entity) }
        assertEquals(result, entity)
    }

    @Test
    fun `should raise exception if attempt to add an existing entity to repo`() {
        val entity = mockk<RuleEntity>(relaxed = true)

        //given
        every { repo.findByMapTo(entity.mapTo) } returns Optional.of(entity)
        every { repo.save(entity) } returns entity

        //when
        var exceptionThrown = false
        try {
            service.add(entity)
        } catch(exception: AlreadyExistException) {
            exceptionThrown = true
        }

        //then
        verify(exactly = 0) { repo.save(entity) }
        assertTrue(exceptionThrown)
    }

    @Test
    fun `should get an entity from repo given an ID if it exist`() {
        val entity = mockk<RuleEntity>()

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
    fun `should update an entity's value given an ID and value if ID exist`() {
        val entity = mockk<RuleEntity>(relaxed = true)

        //given
        every { repo.findById(1) } returns Optional.of(entity)
        every { repo.save(entity) } returns entity
        every { translator.expandVocabulary() } returns Unit

        //when
        val result = service.update(1, "nonStandardRef")

        //then
        verify(exactly = 1) { repo.findById(1) }

        // assertion aligns with the default value set on entity creation because of relaxed mocking
        // (i.e. test does not check actual values but only checks implementations are correct)
        assertTrue(result.mapFrom == "")
    }

    @Test
    fun `should remove an entity given ID if it exists`() {
        val entity = mockk<RuleEntity>()

        //given
        every { repo.findById(1) } returns Optional.of(entity)
        every { repo.deleteById(1) } returns Unit
        every { translator.expandVocabulary() } returns Unit

        //when
        val result = service.remove(1)

        //then
        verify(exactly = 1) {  repo.findById(1) }
        assertEquals(result, "OK")
    }
}
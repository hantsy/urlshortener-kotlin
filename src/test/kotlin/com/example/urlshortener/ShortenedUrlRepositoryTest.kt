package com.example.urlshortener


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ShortenedUrlRepositoryTest {

    @Autowired
    private lateinit var registeredUrlRepository: ShortenedUrlRepository


    @Autowired
    private lateinit var tem: TestEntityManager


    @BeforeEach
    fun `setup for each tests`() {
        tem.persist(ShortenedUrl(id ="testid", url = "url"))
    }

    @AfterEach
    fun `clear data for each tests`() {
    }

    @Test
    fun `ShortenedUrlRepository bean should exists`() {
        assertNotNull(this.registeredUrlRepository)
    }

    @Test
    fun `findAll should not be empty`() {
        assertTrue(this.registeredUrlRepository.findAll().isNotEmpty())
    }

}

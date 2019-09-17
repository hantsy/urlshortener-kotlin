package com.example.urlshortener


import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verifyNoMoreInteractions
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(controllers = [UrlShortenerController::class])
class UrlShortenerControllerTest {

    @MockBean
    private lateinit var registeredUrlRepository: ShortenedUrlRepository

    @MockBean
    private lateinit var shortener: UrlShortener

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()


    @Test
    fun `findAll should not be empty`() {
        val data = ShortenedUrl(id = "testid", url = "url")

        given(this.registeredUrlRepository.findAll())
                .willReturn(listOf(data))

        this.mockMvc
                .perform(
                        get("/")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(status().isOk)

        verify(this.registeredUrlRepository, times(1)).findAll()
        verifyNoMoreInteractions(this.registeredUrlRepository)
    }


    @Test
    fun `generate shorten url and save`() {
        val data = ShortenedUrl(id = "testid", url = "url")

        given(this.registeredUrlRepository.save(ArgumentMatchers.any(ShortenedUrl::class.java)))
                .willReturn(data)
        given(this.shortener.shorten("url")).willReturn("testid")

        val req = UrlShortenRequest("test.com")

        this.mockMvc
                .perform(
                        post("/")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(this.objectMapper.writeValueAsBytes(req))

                )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("\$.id", `is`("testid")))

        verify(this.registeredUrlRepository, times(1)).save(ArgumentMatchers.any(ShortenedUrl::class.java))
        verify(this.shortener, times(1)).shorten(anyString())
        verifyNoMoreInteractions(this.registeredUrlRepository)
        verifyNoMoreInteractions(this.shortener)

    }

    @Test
    fun `get shortenurl should redirect the originurl`() {
        val data = ShortenedUrl(id = "testid", url = "url")

        given(this.registeredUrlRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Optional.of(data))

        this.mockMvc
                .perform(
                        get("/testid")
                                .accept(MediaType.APPLICATION_JSON_UTF8)

                )
                .andExpect(status().is3xxRedirection)
                .andExpect(header().string("Location", "url"))

        verify(this.registeredUrlRepository, times(1)).findById(ArgumentMatchers.anyString())
        verifyNoMoreInteractions(this.registeredUrlRepository)

    }

    @Test
    fun `get shortenurl for none existing should return 404`() {
        given(this.registeredUrlRepository.findById(ArgumentMatchers.anyString()))
                .willReturn(Optional.empty())

        this.mockMvc
                .perform(
                        get("/testid")
                                .accept(MediaType.APPLICATION_JSON_UTF8)

                )
                .andExpect(status().isNotFound)

        verify(this.registeredUrlRepository, times(1)).findById(ArgumentMatchers.anyString())
        verifyNoMoreInteractions(this.registeredUrlRepository)
    }

}

package com.example.urlshortener

import org.apache.commons.lang3.RandomStringUtils
import org.hibernate.validator.constraints.URL
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.persistence.Entity
import javax.persistence.Id
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@SpringBootApplication
class UrlShortenerKotlinApplication

fun main(args: Array<String>) {
    runApplication<UrlShortenerKotlinApplication>(*args)
}


@RestController
@RequestMapping("")
class UrlShortenerController(
        private val urls: ShortenedUrlRepository,
        private val shortener: UrlShortener) {

    @GetMapping
    fun all(): ResponseEntity<MutableList<ShortenedUrl>> = ok(this.urls.findAll())

    @PostMapping
    fun save(@RequestBody @Valid res: UrlShortenRequest, req: HttpServletRequest): ResponseEntity<ShortenedUrl> {
        val id = this.shortener.shorten(res.url)
        val registeredUrl = ShortenedUrl(id = id, url = res.url)
        val savedRegisteredUrl = this.urls.save(registeredUrl)
        return created(ServletUriComponentsBuilder.fromContextPath(req).path("/$id").build().toUri())
                .body(savedRegisteredUrl)
    }

    @GetMapping("/{s}")
    fun get(@PathVariable("s") shortenUrl: String): ResponseEntity<String> {
        val registeredUrl = this.urls.findById(shortenUrl)
        return registeredUrl
                .map {
                    status(HttpStatus.PERMANENT_REDIRECT)
                            .header(HttpHeaders.LOCATION, it.url)
                            .build<String>()
                }
                .orElse(
                        notFound().build()
                )

    }
}

@Component
class SimpleUrlShortener : UrlShortener {
    override fun shorten(url:String): String {
        return RandomStringUtils.random(10, true, true)
    }
}

interface UrlShortener {
    fun shorten(url:String): String
}


data class UrlShortenRequest(@URL val url: String)

interface ShortenedUrlRepository : JpaRepository<ShortenedUrl, String>

@Entity
data class ShortenedUrl(@Id var id: String? = null, var url: String? = null)
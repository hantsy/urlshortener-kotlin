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
import java.net.URI
import javax.persistence.Entity
import javax.persistence.Id
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@SpringBootApplication
class UrlshortenerKotlinApplication

fun main(args: Array<String>) {
    runApplication<UrlshortenerKotlinApplication>(*args)
}


@RestController
@RequestMapping("")
class UrlShortenerController(
        private val urls: RegisteredUrlRepository,
        private val shortener: UrlShortener) {

    @GetMapping
    fun all(): ResponseEntity<MutableList<RegisteredUrl>> = ok(this.urls.findAll())

    @PostMapping
    fun save(@RequestBody @Valid res: UrlShortenRequest, req: HttpServletRequest): ResponseEntity<RegisteredUrl> {
        val id = this.shortener.generate()
        val registeredUrl = RegisteredUrl(id = id, originUrl = res.url)
        val savedRegisteredUrl = this.urls.save(registeredUrl)
        return created(ServletUriComponentsBuilder.fromContextPath(req).path("/$id").build().toUri())
                .body(savedRegisteredUrl)
    }

    @GetMapping("/{s}")
    fun get(@PathVariable("s") shortenUrl: String): ResponseEntity<String> {
        val registeredUrl = this.urls.findById(shortenUrl)
        return registeredUrl.map {
            status(HttpStatus.PERMANENT_REDIRECT)
                    .header(HttpHeaders.LOCATION, it.originUrl)
                    .build<String>()
        }
                .orElse(
                        notFound().build()
                )

    }
}

@Component
class SimpleUrlShortener : UrlShortener {
    override fun generate(): String {
        return RandomStringUtils.random(10, true, true)
    }
}

interface UrlShortener {
    fun generate(): String
}


data class UrlShortenRequest(@URL val url: String)

interface RegisteredUrlRepository : JpaRepository<RegisteredUrl, String>

@Entity
data class RegisteredUrl(@Id var id: String? = null, var originUrl: String? = null)
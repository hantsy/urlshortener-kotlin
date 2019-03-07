package com.example.urlshortener


import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private var token: String? = null

    @BeforeAll
    fun setup() {
        RestAssured.port = this.port
    }

    @Test
    fun `get all registered urls should be ok`() {
        //@formatter:off
        given()
                .accept(io.restassured.http.ContentType.JSON)

        .`when`()
                .get("/")

        .then()
                .assertThat().statusCode(HttpStatus.SC_OK)
        //@formatter:on
    }

    @Test
    fun `generate and save shorturl and  get shorten url`() {
        val req = UrlShortenRequest("http://www.test.com")
        //@formatter:off
        val responseSave = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(this.objectMapper.writeValueAsBytes(req))
        .`when`()
                .post("/")
        .andReturn()
        //@formatter:on

        val savedLocation = responseSave.getHeader("Location")

        responseSave.then().assertThat().statusCode(HttpStatus.SC_CREATED)


        //@formatter:off
        given()
                .accept(io.restassured.http.ContentType.JSON)

        .`when`()
                .get(savedLocation)

        .then()
                .assertThat().statusCode(308)
                .assertThat().header("Location", "http://www.test.com")
        //@formatter:on

    }
}

package com.template

import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActuatorTest : ApiIntegrationTest() {

    @Test
    fun healthCheckApiIsOpen() {
        val requestEntity = RequestEntity.get(URI.create("/actuator/health"))
            .build()
        val responseEntity = restTemplate.exchange(requestEntity, String::class.java)
        val response = responseEntity.body
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertTrue(response.contains("status"))
        assertTrue(response.contains("UP"))
    }

    @Test
    fun infoApiIsOpen() {
        val requestEntity = RequestEntity.get(URI.create("/actuator/info"))
            .build()
        val responseEntity = restTemplate.exchange(requestEntity, String::class.java)
        val response = responseEntity.body
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertTrue(response.contains("author"))
        assertTrue(response.contains("version"))
        assertTrue(response.contains("description"))
        assertTrue(response.contains("more_info"))
    }
}
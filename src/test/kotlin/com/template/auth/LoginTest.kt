package com.template.auth

import com.template.ApiIntegrationTest
import com.template.auth.dto.LoginRequestDto
import com.template.auth.dto.LoginResponseDto
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.lang.IllegalArgumentException
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LoginTest : ApiIntegrationTest() {

    companion object {
        const val WRONG_EMAIL = "wrong@wrong.com"
        const val WRONG_PASSWORD = "wrongPassword"
    }

    private fun getLoginRequestEntity(requestDto: LoginRequestDto): RequestEntity<LoginRequestDto> {
        return RequestEntity.post(URI.create("/v1/auth/login"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestDto)
    }

    @Test
    fun thisTestShouldFail() {
        throw IllegalArgumentException("FAIL!!")
    }

    private fun getLoginRequestDto(email: String, password: String): LoginRequestDto {
        val requestDto = LoginRequestDto()
        requestDto.email = email
        requestDto.password = password
        return requestDto
    }

    @Test
    fun login_responseIsOkIfAllConditionsAreRight() {
        val userId = getUserId()
        val requestDto = getLoginRequestDto(EMAIL, PASSWORD)
        val requestEntity = getLoginRequestEntity(requestDto)
        val responseEntity = restTemplate.exchange(requestEntity, LoginResponseDto::class.java)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        val responseBody = responseEntity.body
        assertNotNull(responseBody.accessToken)
        assertNotNull(responseBody.refreshToken)

        val accessToken = responseBody.accessToken
        val refreshToken = responseBody.refreshToken

        assertEquals(userId, jwtTokenUtil.extractUserId(accessToken))
        assertEquals(userId, jwtTokenUtil.extractUserId(refreshToken))
    }

    @Test
    fun login_responseIsNotFoundIfEmailIsWrong() {
        val requestDto = getLoginRequestDto(WRONG_EMAIL, PASSWORD)
        val requestEntity = getLoginRequestEntity(requestDto)

        assertResponseStatus(HttpStatus.NOT_FOUND, requestEntity)
    }

    @Test
    fun login_responseIsNotFoundIfPasswordIsWrong() {
        val requestDto = getLoginRequestDto(EMAIL, WRONG_PASSWORD)
        val requestEntity = getLoginRequestEntity(requestDto)

        assertResponseStatus(HttpStatus.NOT_FOUND, requestEntity)
    }
}
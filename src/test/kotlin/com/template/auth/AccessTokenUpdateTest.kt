package com.template.auth

import com.template.ApiIntegrationTest
import com.template.auth.dto.AccessTokenUpdateRequestDto
import com.template.auth.dto.AccessTokenUpdateResponseDto
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.net.URI
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AccessTokenUpdateTest : ApiIntegrationTest() {

    companion object {
        private const val WRONG_TOKEN = "wrongToken"
    }

    @Value("\${jwt.secret}")
    lateinit var secretKey: String


    private fun getTokenUpdateRequestEntity(requestDto: AccessTokenUpdateRequestDto): RequestEntity<AccessTokenUpdateRequestDto> {
        return RequestEntity.post(URI.create("/v1/auth/update-token"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestDto)
    }

    @Test
    fun updateToken_responseIsOkIfAllConditionsAreRight() {
        val userId = getUserId()
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(userId))
        val requestEntity = getTokenUpdateRequestEntity(requestDto)

        val responseEntity = restTemplate.exchange(requestEntity, AccessTokenUpdateResponseDto::class.java)
        val responseBody = responseEntity.body

        assertEquals(HttpStatus.OK, responseEntity.statusCode)

        assertFalse(jwtTokenUtil.isTokenExpired(responseBody.accessToken))
    }

    @Test
    fun updateToken_responseIsUnAuthorizedIfRefreshTokenIsMalformed() {
        val requestDto = AccessTokenUpdateRequestDto(WRONG_TOKEN)
        val requestEntity = getTokenUpdateRequestEntity(requestDto)

        assertResponseStatus(HttpStatus.UNAUTHORIZED, requestEntity)
    }

    @Test
    fun updateToken_responseIsUnAuthorizedIfUserIdIsInvalid() {
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(-1))
        val requestEntity = getTokenUpdateRequestEntity(requestDto)

        assertResponseStatus(HttpStatus.UNAUTHORIZED, requestEntity)
    }

    @Test
    fun updateToken_responseIsUnAuthorizedIfAccessTokenIsExpired() {
        val userId = getUserId()
        val requestDto = AccessTokenUpdateRequestDto(generateExpiredRefreshToken(userId))
        val requestEntity = getTokenUpdateRequestEntity(requestDto)

        assertResponseStatus(HttpStatus.UNAUTHORIZED, requestEntity)
    }

    private fun generateExpiredRefreshToken(userId: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = userId
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis() - 86400000 * 8))
            .setExpiration(Date(System.currentTimeMillis() - 86400000 * 7))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}
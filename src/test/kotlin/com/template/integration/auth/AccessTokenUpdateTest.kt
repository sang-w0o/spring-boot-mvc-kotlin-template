package com.template.integration.auth

import com.jayway.jsonpath.JsonPath
import com.template.integration.ApiIntegrationTest
import com.template.auth.dto.AccessTokenUpdateRequestDto
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import java.net.URI
import java.util.Date

class AccessTokenUpdateTest : ApiIntegrationTest() {

    companion object {
        private const val WRONG_TOKEN = "wrongToken"
    }

    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    private fun apiCall(requestDto: AccessTokenUpdateRequestDto): ResultActionsDsl {
        return mockMvc.post(URI.create("/v1/auth/update-token")) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestDto)
        }
    }

    @Test
    fun updateToken_responseIsOkIfAllConditionsAreRight() {
        val userId = getUserId()
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(userId))

        val result = apiCall(requestDto).andExpect {
            status { isOk() }
            jsonPath("accessToken") { exists() }
        }.andReturn()

        val accessToken = JsonPath.read<String>(result.response.contentAsString, "$.accessToken")
        assertFalse(jwtTokenUtil.isTokenExpired(accessToken))
    }

    @Test
    fun updateToken_responseIsUnAuthorizedIfRefreshTokenIsMalformed() {
        val requestDto = AccessTokenUpdateRequestDto(WRONG_TOKEN)
        apiCall(requestDto).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this)
        }
    }

    @Test
    fun updateToken_responseIsUnAuthorizedIfUserIdIsInvalid() {
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(-1))
        apiCall(requestDto).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this)
        }
    }

    @Test
    fun updateToken_responseIsUnAuthorizedIfAccessTokenIsExpired() {
        val userId = getUserId()
        val requestDto = AccessTokenUpdateRequestDto(generateExpiredRefreshToken(userId))
        apiCall(requestDto).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this)
        }
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

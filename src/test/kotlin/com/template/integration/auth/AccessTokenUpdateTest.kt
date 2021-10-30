package com.template.integration.auth

import com.jayway.jsonpath.JsonPath
import com.template.auth.dto.AccessTokenUpdateRequestDto
import com.template.integration.ApiIntegrationTest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
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

    @DisplayName("Success")
    @Test
    fun updateToken_responseIsOkIfAllConditionsAreRight() {
        val userId = getUserId()
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(userId))

        val result = apiCall(requestDto).andExpect {
            status { isOk() }
            jsonPath("accessToken") { exists() }
        }.andReturn()

        val accessToken = JsonPath.read<String>(result.response.contentAsString, "$.accessToken")
        jwtTokenUtil.isTokenExpired(accessToken) shouldBe false
    }

    @DisplayName("실패 - 토큰이 잘못된 경우")
    @Test
    fun updateToken_responseIsUnAuthorizedIfRefreshTokenIsMalformed() {
        val requestDto = AccessTokenUpdateRequestDto(WRONG_TOKEN)
        apiCall(requestDto).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "잘못된 형식의 Jwt 토큰입니다.")
        }
    }

    @DisplayName("실패 - 잘못된 userId")
    @Test
    fun updateToken_responseIsUnAuthorizedIfUserIdIsInvalid() {
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(-1))
        apiCall(requestDto).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "Unauthorized User Id.")
        }
    }

    @DisplayName("실패 - 만료된 토큰")
    @Test
    fun updateToken_responseIsUnAuthorizedIfAccessTokenIsExpired() {
        val userId = getUserId()
        val requestDto = AccessTokenUpdateRequestDto(generateExpiredRefreshToken(userId))
        apiCall(requestDto).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "Jwt 토큰이 만료되었습니다.")
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

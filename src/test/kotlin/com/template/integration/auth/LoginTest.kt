package com.template.integration.auth

import com.jayway.jsonpath.JsonPath
import com.template.auth.dto.LoginRequestDto
import com.template.integration.ApiIntegrationTest
import com.template.util.EMAIL
import com.template.util.PASSWORD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import java.net.URI

class LoginTest : ApiIntegrationTest() {

    companion object {
        const val WRONG_EMAIL = "wrong@wrong.com"
        const val WRONG_PASSWORD = "wrongPassword"
    }

    private fun getLoginRequestDto(email: String, password: String): LoginRequestDto {
        val requestDto = LoginRequestDto()
        requestDto.email = email
        requestDto.password = password
        return requestDto
    }

    private fun apiCall(requestDto: LoginRequestDto): ResultActionsDsl {
        return mockMvc.post(URI.create("/v1/auth/login")) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(requestDto)
        }
    }

    @Test
    @DisplayName("로그인 성공")
    fun login_responseIsOkIfAllConditionsAreRight() {
        val userId = getUserId()
        val requestDto = getLoginRequestDto(EMAIL, PASSWORD)

        val result = apiCall(requestDto).andExpect {
            status { isOk() }
            jsonPath("accessToken") { exists() }
            jsonPath("refreshToken") { exists() }
        }.andReturn()

        val accessToken = JsonPath.read<String>(result.response.contentAsString, "$.accessToken")
        val refreshToken = JsonPath.read<String>(result.response.contentAsString, "$.refreshToken")

        assertEquals(userId, jwtTokenUtil.extractUserId(accessToken))
        assertEquals(userId, jwtTokenUtil.extractUserId(refreshToken))
    }

    @Test
    @DisplayName("로그인 실패 - 없는 이메일")
    fun login_responseIsNotFoundIfEmailIsWrong() {
        val requestDto = getLoginRequestDto(WRONG_EMAIL, PASSWORD)

        apiCall(requestDto).andExpect {
            status { isNotFound() }
            assertErrorResponse(this, "이메일 또는 비밀번호가 잘못되었습니다.")
        }
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 오류")
    fun login_responseIsNotFoundIfPasswordIsWrong() {
        val requestDto = getLoginRequestDto(EMAIL, WRONG_PASSWORD)
        apiCall(requestDto).andExpect {
            status { isNotFound() }
            assertErrorResponse(this, "이메일 또는 비밀번호가 잘못되었습니다.")
        }
    }
}

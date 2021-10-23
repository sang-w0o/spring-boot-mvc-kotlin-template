package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.util.TestUtils.EMAIL
import com.template.util.TestUtils.NAME
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import java.net.URI

class UserGetInfoTest : ApiIntegrationTest() {

    private fun apiCall(accessToken: String): ResultActionsDsl {
        return mockMvc.get(URI.create("/v1/user/")) {
            header("Authorization", "Bearer $accessToken")
        }
    }

    @DisplayName("Success")
    @Test
    fun getInfo_responseIsOkIfAllConditionsAreRight() {
        val userId = getUserId()
        val accessToken = jwtTokenUtil.generateAccessToken(userId)
        apiCall(accessToken).andExpect {
            status { isOk() }
            jsonPath("email") { value(EMAIL) }
            jsonPath("id") { value(userId) }
            jsonPath("name") { value(NAME) }
        }.andReturn()
    }

    @DisplayName("Fail - Invalid userId")
    @Test
    fun failWithInvalidUserId() {
        val accessToken = jwtTokenUtil.generateAccessToken(-1)
        apiCall(accessToken).andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "Invalid userId.")
        }
    }

    @DisplayName("Fail - No Authorization header present.")
    @Test
    fun failWithAbsentAuthorizationHeader() {
        apiCall("").andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "Jwt 헤더 값이 잘못되었습니다.")
        }
    }

    @DisplayName("Fail - Wrong Authorization header scheme.")
    @Test
    fun failWithWrongAuthorizationHeaderScheme() {
        mockMvc.get(URI.create("/v1/user/")) {
            header("Authorization", "Wrong accessToken")
        }.andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "Scheme is not Bearer.")
        }
    }

    @DisplayName("Fail - AccessToken malformed.")
    @Test
    fun failWithMalformedAccessToken() {
        apiCall("MalformedToken").andExpect {
            status { isUnauthorized() }
            assertErrorResponse(this, "잘못된 형식의 Jwt 토큰입니다.")
        }
    }
}

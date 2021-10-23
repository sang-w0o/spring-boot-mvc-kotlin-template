package com.template.unit.auth

import com.template.auth.dto.AccessTokenUpdateRequestDto
import com.template.auth.exception.AuthenticateException
import com.template.auth.service.AuthService
import com.template.auth.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.assertFailsWith

class AccessTokenUpdateServiceUnitTest : BaseUnitTest() {

    private lateinit var authService: AuthService

    @MockBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    private val encoder = BCryptPasswordEncoder(10)

    @BeforeEach
    fun setUp() {
        `when`(jwtTokenUtil.generateAccessToken(anyInt())).thenReturn(TOKEN)
        `when`(jwtTokenUtil.generateRefreshToken(anyInt())).thenReturn(TOKEN)
        authService = AuthService(jwtTokenUtil, userRepository, encoder)
    }

    @DisplayName("AccessToken 갱신 성공")
    @Test
    fun updateAccessToken_Success() {
        `when`(userRepository.existsById(anyInt())).thenReturn(true)
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(USER_ID))
        val result = authService.updateAccessToken(requestDto)
        assertFalse(jwtTokenUtil.isTokenExpired(result.accessToken))
    }

    @DisplayName("AccessToken 갱신 실패 - 잘못된 userId인 경우")
    @Test
    fun updateAccessToken_FailWrongUserId() {
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(USER_ID))
        val exception = assertThrows <AuthenticateException> { authService.updateAccessToken(requestDto) }
        assertEquals("Unauthorized User Id.", exception.message!!)
    }

    @DisplayName("AccessToken 갱신 실패 - 만료된 refreshToken이 주어진 경우")
    @Test
    fun updateAccessToken_FailIfExpiredRefreshToken() {
        val requestDto = AccessTokenUpdateRequestDto(jwtTokenUtil.generateRefreshToken(USER_ID))
        `when`(jwtTokenUtil.isTokenExpired(anyString())).thenReturn(true)
        val exception = assertFailsWith<AuthenticateException> { authService.updateAccessToken(requestDto) }
        assertEquals("RefreshToken has been expired.", exception.message!!)
    }
}

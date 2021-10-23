package com.template.unit.auth

import com.template.auth.dto.LoginRequestDto
import com.template.auth.exception.LoginException
import com.template.auth.service.AuthService
import com.template.auth.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.util.TestUtils.EMAIL
import com.template.util.TestUtils.NAME
import com.template.util.TestUtils.PASSWORD
import com.template.util.TestUtils.USER_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Optional
import kotlin.test.assertFailsWith

class LoginUnitTest : BaseUnitTest() {

    private lateinit var authService: AuthService

    @MockBean
    private lateinit var encoder: BCryptPasswordEncoder

    @SpyBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        authService = AuthService(jwtTokenUtil, userRepository, encoder)
    }

    @DisplayName("로그인 성공")
    @Test
    fun login_Success() {
        val user = User(NAME, EMAIL, PASSWORD)
        user.id = USER_ID
        `when`(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user))
        `when`(encoder.matches(anyString(), anyString())).thenReturn(true)
        val requestDto = LoginRequestDto(EMAIL, PASSWORD)
        val responseDto = authService.login(requestDto)
        assertFalse(jwtTokenUtil.isTokenExpired(responseDto.accessToken))
        assertFalse(jwtTokenUtil.isTokenExpired(responseDto.refreshToken))
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    fun login_FailIfWrongPassword() {
        `when`(encoder.matches(anyString(), anyString())).thenReturn(false)
        val requestDto = LoginRequestDto(EMAIL, PASSWORD)
        val exception = assertFailsWith<LoginException> { authService.login(requestDto) }
        assertEquals("이메일 또는 비밀번호가 잘못되었습니다.", exception.message)
    }

    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    @Test
    fun login_FailIfWrongEmail() {
        `when`(userRepository.findByEmail(anyString())).thenReturn(Optional.empty())
        val requestDto = LoginRequestDto(EMAIL, PASSWORD)
        val exception = assertFailsWith<LoginException> { authService.login(requestDto) }
        assertEquals("이메일 또는 비밀번호가 잘못되었습니다.", exception.message)
    }
}

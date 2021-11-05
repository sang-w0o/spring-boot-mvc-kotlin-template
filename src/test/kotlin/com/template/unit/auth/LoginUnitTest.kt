package com.template.unit.auth

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import com.template.auth.dto.LoginRequestDto
import com.template.auth.exception.LoginException
import com.template.auth.service.AuthService
import com.template.auth.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.util.EMAIL
import com.template.util.NAME
import com.template.util.PASSWORD
import com.template.util.USER_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.Optional

class LoginUnitTest : BaseUnitTest() {

    private lateinit var authService: AuthService

    @MockkBean
    private lateinit var encoder: BCryptPasswordEncoder

    @SpykBean
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
        every { userRepository.findByEmail(any()) } returns Optional.of(user)
        every { encoder.matches(any(), any()) } returns true
        val requestDto = LoginRequestDto(EMAIL, PASSWORD)
        val responseDto = authService.login(requestDto)
        jwtTokenUtil.isTokenExpired(responseDto.accessToken) shouldBe false
        jwtTokenUtil.isTokenExpired(responseDto.refreshToken) shouldBe false
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    fun login_FailIfWrongPassword() {
        every { encoder.matches(any(), any()) } returns false
        every { userRepository.findByEmail(any()) } returns Optional.of(getMockUser())
        val requestDto = LoginRequestDto(EMAIL, PASSWORD)
        val exception = shouldThrow<LoginException> { authService.login(requestDto) }
        exception.message shouldBe "이메일 또는 비밀번호가 잘못되었습니다."
    }

    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    @Test
    fun login_FailIfWrongEmail() {
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        val requestDto = LoginRequestDto(EMAIL, PASSWORD)
        val exception = shouldThrow<LoginException> { authService.login(requestDto) }
        exception.message shouldBe "이메일 또는 비밀번호가 잘못되었습니다."
    }
}

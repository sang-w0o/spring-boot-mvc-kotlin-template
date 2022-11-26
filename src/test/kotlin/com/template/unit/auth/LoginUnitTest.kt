package com.template.unit.auth

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import com.template.auth.exception.LoginException
import com.template.auth.service.AuthCommandHandler
import com.template.auth.service.impl.AuthCommandHandlerImpl
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

    private lateinit var authCommandHandler: AuthCommandHandler

    @MockkBean
    private lateinit var encoder: BCryptPasswordEncoder

    @SpykBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        authCommandHandler = AuthCommandHandlerImpl(jwtTokenUtil, userRepository, encoder)
    }

    @DisplayName("로그인 성공")
    @Test
    fun login_Success() {
        val user = User(NAME, EMAIL, PASSWORD)
        user.id = USER_ID
        every { userRepository.findByEmail(any()) } returns Optional.of(user)
        every { encoder.matches(any(), any()) } returns true
        val result = authCommandHandler.login(EMAIL, PASSWORD)
        jwtTokenUtil.isTokenExpired(result.first) shouldBe false
        jwtTokenUtil.isTokenExpired(result.second) shouldBe false
    }

    @DisplayName("로그인 실패 - 비밀번호 불일치")
    @Test
    fun login_FailIfWrongPassword() {
        every { encoder.matches(any(), any()) } returns false
        every { userRepository.findByEmail(any()) } returns Optional.of(getMockUser())
        val exception = shouldThrow<LoginException> { authCommandHandler.login(EMAIL, PASSWORD) }
        exception.message shouldBe "이메일 또는 비밀번호가 잘못되었습니다."
    }

    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    @Test
    fun login_FailIfWrongEmail() {
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        val exception = shouldThrow<LoginException> { authCommandHandler.login(EMAIL, PASSWORD) }
        exception.message shouldBe "이메일 또는 비밀번호가 잘못되었습니다."
    }
}

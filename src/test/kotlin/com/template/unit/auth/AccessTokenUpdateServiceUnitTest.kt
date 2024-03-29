package com.template.unit.auth

import com.ninjasquad.springmockk.MockkBean
import com.template.auth.exception.AuthenticateException
import com.template.auth.service.AuthCommandHandler
import com.template.auth.service.impl.AuthCommandHandlerImpl
import com.template.auth.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.util.TOKEN
import com.template.util.USER_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AccessTokenUpdateServiceUnitTest : BaseUnitTest() {

    private lateinit var authCommandHandler: AuthCommandHandler

    @MockkBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    private val encoder = BCryptPasswordEncoder(10)

    @BeforeEach
    fun setUp() {
        every { jwtTokenUtil.generateAccessToken(any()) } returns TOKEN
        every { jwtTokenUtil.generateRefreshToken(any()) } returns TOKEN
        every { jwtTokenUtil.extractUserId(any()) } returns USER_ID
        every { jwtTokenUtil.isTokenExpired(any()) } returns false
        authCommandHandler = AuthCommandHandlerImpl(jwtTokenUtil, userRepository, encoder)
    }

    @DisplayName("AccessToken 갱신 성공")
    @Test
    fun updateAccessToken_Success() {
        every { userRepository.existsById(any()) } returns true
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        val result = authCommandHandler.updateAccessToken(refreshToken)
        jwtTokenUtil.isTokenExpired(result) shouldBe false
    }

    @DisplayName("AccessToken 갱신 실패 - 잘못된 userId인 경우")
    @Test
    fun updateAccessToken_FailWrongUserId() {
        every { userRepository.existsById(any()) } returns false
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        val exception = assertThrows <AuthenticateException> { authCommandHandler.updateAccessToken(refreshToken) }
        exception.message shouldBe "Unauthorized User Id."
    }

    @DisplayName("AccessToken 갱신 실패 - 만료된 refreshToken이 주어진 경우")
    @Test
    fun updateAccessToken_FailIfExpiredRefreshToken() {
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        every { jwtTokenUtil.isTokenExpired(any()) } returns true
        val exception = shouldThrow<AuthenticateException> { authCommandHandler.updateAccessToken(refreshToken) }
        exception.message shouldBe "RefreshToken has been expired."
    }
}

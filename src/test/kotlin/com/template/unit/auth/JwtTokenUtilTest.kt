package com.template.unit.auth

import com.template.auth.exception.AuthenticateException
import com.template.auth.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.service.dto.UserDto
import com.template.util.EXTRA_TIME
import com.template.util.USER_ID
import com.template.util.generateExpiredToken
import com.template.util.generateOtherSignatureToken
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class JwtTokenUtilTest : BaseUnitTest() {

    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        jwtTokenUtil = JwtTokenUtil(userRepository, jwtProperties)
    }

    @DisplayName("AccessToken 생성")
    @Test
    fun accessTokenIsCreated() {
        val accessToken = jwtTokenUtil.generateAccessToken(USER_ID)
        jwtTokenUtil.isTokenExpired(accessToken) shouldBe false
    }

    @DisplayName("RefreshToken 생성")
    @Test
    fun refreshTokenIsCreated() {
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        jwtTokenUtil.isTokenExpired(refreshToken) shouldBe false
    }

    @DisplayName("만료된 AccessToken 검증")
    @Test
    fun oldAccessTokenIsExpired() {
        val oldAccessToken = generateExpiredToken(jwtProperties.accessTokenExp, jwtProperties.secret)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(oldAccessToken) }
        exception.message shouldBe "Jwt 토큰이 만료되었습니다."
    }

    @DisplayName("만료된 RefreshToken 검증")
    @Test
    fun oldRefreshTokenIsExpired() {
        val oldRefreshToken = generateExpiredToken(jwtProperties.refreshTokenExp, jwtProperties.secret)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(oldRefreshToken) }
        exception.message shouldBe "Jwt 토큰이 만료되었습니다."
    }

    @DisplayName("잘못된 형식의 Jwt 토큰")
    @Test
    fun wrongToken() {
        val wrongToken = "WRONG TOKEN"
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        exception.message shouldBe "잘못된 형식의 Jwt 토큰입니다."
    }

    @DisplayName("Signature가 잘못된 Jwt가 주어진 경우")
    @Test
    fun wrongSignatureToken() {
        val wrongToken = generateOtherSignatureToken(jwtProperties.accessTokenExp)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        exception.message shouldBe "Jwt Signature이 잘못된 값입니다."
    }

    @DisplayName("Jwt Payload에 userId가 없는 경우")
    @Test
    fun jwtWithoutUserIdInPayload() {
        val wrongToken = Jwts.builder()
            .setClaims(mutableMapOf())
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + EXTRA_TIME))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret)
            .compact()
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.extractUserId(wrongToken) }
        exception.message shouldBe "JWT Claim에 userId가 없습니다."
    }

    @DisplayName("정상적인 token일 경우 검증 성공")
    @Test
    fun correctTokenVerifySuccess() {
        val user = getMockUser()
        every { userRepository.findById(any()) } returns Optional.of(user)
        val accessToken = jwtTokenUtil.generateAccessToken(user.id!!)
        val authentication = jwtTokenUtil.verify(accessToken)
        authentication.principal.shouldBeInstanceOf<UserDto>()
        val userDto = authentication.principal as UserDto
        userDto.name shouldBe user.name
        userDto.email shouldBe user.email
        userDto.password shouldBe user.password
        userDto.id shouldBe user.id
    }

    @DisplayName("존재하지 않는 userId인 경우 검증 실패")
    @Test
    fun tokenWithInvalidUserId() {
        every { userRepository.findById(any()) } returns Optional.empty()
        val accessToken = jwtTokenUtil.generateAccessToken(USER_ID)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(accessToken) }
        exception.message shouldBe "Invalid userId."
    }
}

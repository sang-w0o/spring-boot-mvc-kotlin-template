package com.template.unit.auth

import com.template.auth.exception.AuthenticateException
import com.template.auth.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.dto.UserDto
import com.template.util.TestUtils.EXTRA_TIME
import com.template.util.TestUtils.USER_ID
import com.template.util.TestUtils.generateExpiredToken
import com.template.util.TestUtils.generateOtherSignatureToken
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
        assertFalse(jwtTokenUtil.isTokenExpired(accessToken))
    }

    @DisplayName("RefreshToken 생성")
    @Test
    fun refreshTokenIsCreated() {
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        assertFalse(jwtTokenUtil.isTokenExpired(refreshToken))
    }

    @DisplayName("만료된 AccessToken 검증")
    @Test
    fun oldAccessTokenIsExpired() {
        val oldAccessToken = generateExpiredToken(jwtProperties.accessTokenExp, jwtProperties.secret)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(oldAccessToken) }
        assertEquals("Jwt 토큰이 만료되었습니다.", exception.message!!)
    }

    @DisplayName("만료된 RefreshToken 검증")
    @Test
    fun oldRefreshTokenIsExpired() {
        val oldRefreshToken = generateExpiredToken(jwtProperties.refreshTokenExp, jwtProperties.secret)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(oldRefreshToken) }
        assertEquals("Jwt 토큰이 만료되었습니다.", exception.message!!)
    }

    @DisplayName("잘못된 형식의 Jwt 토큰")
    @Test
    fun wrongToken() {
        val wrongToken = "WRONG TOKEN"
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        assertEquals("잘못된 형식의 Jwt 토큰입니다.", exception.message!!)
    }

    @DisplayName("Signature가 잘못된 Jwt가 주어진 경우")
    @Test
    fun wrongSignatureToken() {
        val wrongToken = generateOtherSignatureToken(jwtProperties.accessTokenExp)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        assertEquals("Jwt Signature이 잘못된 값입니다.", exception.message!!)
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
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.extractUserId(wrongToken) }
        assertEquals("JWT Claim에 userId가 없습니다.", exception.message!!)
    }

    @DisplayName("정상적인 token일 경우 검증 성공")
    @Test
    fun correctTokenVerifySuccess() {
        val user = getMockUser()
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user))
        val accessToken = jwtTokenUtil.generateAccessToken(user.id!!)
        val authentication = jwtTokenUtil.verify(accessToken)
        assertTrue(authentication.principal is UserDto)
        val userDto = authentication.principal as UserDto
        assertEquals(user.name, userDto.name)
        assertEquals(user.email, userDto.email)
        assertEquals(user.password, userDto.password)
        assertEquals(user.id, userDto.id)
    }

    @DisplayName("존재하지 않는 userId인 경우 검증 실패")
    @Test
    fun tokenWithInvalidUserId() {
        `when`(userRepository.findById(anyInt())).thenReturn(Optional.empty())
        val accessToken = jwtTokenUtil.generateAccessToken(USER_ID)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(accessToken) }
        assertEquals("Invalid userId.", exception.message!!)
    }
}

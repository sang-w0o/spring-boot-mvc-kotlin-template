package com.template.unit.auth

import com.template.auth.exception.AuthenticateException
import com.template.auth.tools.JwtProperties
import com.template.auth.tools.JwtTokenUtil
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JwtTokenUtilTest {

    companion object {
        const val USER_ID = 1
        const val EXTRA_TIME = 2000000
        const val SECRET_KEY = "SecretKey"
        const val ACCESS_TOKEN_EXP = 86400000
        const val REFRESH_TOKEN_EXP = 86400000 * 7
    }

    private val jwtProperties = JwtProperties(SECRET_KEY, ACCESS_TOKEN_EXP, REFRESH_TOKEN_EXP)
    private val jwtTokenUtil = JwtTokenUtil(jwtProperties)

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
        val oldAccessToken = generateExpiredToken(jwtProperties.accessTokenExp)
        val exception = assertFailsWith<AuthenticateException>{ jwtTokenUtil.verify(oldAccessToken) }
        assertEquals("Jwt 토큰이 만료되었습니다.", exception.message!!)
    }

    @DisplayName("만료된 RefreshToken 검증")
    @Test
    fun oldRefreshTokenIsExpired() {
        val oldRefreshToken = generateExpiredToken(jwtProperties.refreshTokenExp)
        val exception = assertFailsWith<AuthenticateException>{ jwtTokenUtil.verify(oldRefreshToken) }
        assertEquals("Jwt 토큰이 만료되었습니다.", exception.message!!)
    }

    @DisplayName("잘못된 형식의 Jwt 토큰")
    @Test
    fun wrongToken() {
        val wrongToken = "WRONG TOKEN"
        val exception = assertFailsWith<AuthenticateException>{ jwtTokenUtil.verify(wrongToken) }
        assertEquals("잘못된 형식의 Jwt 토큰입니다.", exception.message!!)
    }

    @DisplayName("Signature가 잘못된 Jwt가 주어진 경우")
    @Test
    fun wrongSignatureToken() {
        val wrongToken = generateOtherSignatureToken(jwtProperties.accessTokenExp)
        val exception = assertFailsWith<AuthenticateException>{ jwtTokenUtil.verify(wrongToken) }
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

    private fun generateExpiredToken(exp: Int): String {
        val realExp = EXTRA_TIME + exp
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = USER_ID
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis() - realExp))
            .setExpiration(Date(System.currentTimeMillis() - EXTRA_TIME))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret)
            .compact()
    }

    private fun generateOtherSignatureToken(exp: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = USER_ID
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + exp))
            .signWith(SignatureAlgorithm.HS256, "Other Signature")
            .compact()
    }
}
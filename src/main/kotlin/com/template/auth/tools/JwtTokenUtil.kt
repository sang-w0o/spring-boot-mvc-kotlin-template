package com.template.auth.tools

import com.template.auth.exception.AuthenticateException
import com.template.security.service.UserDetailsImpl
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.util.*
import java.util.function.Function

@Component
class JwtTokenUtil {

    @Value("\${jwt.secret}")
    lateinit var secretKey: String

    companion object {
        private const val ACCESS_TOKEN_EXP: Int = 86400000
        private const val REFRESH_TOKEN_EXP: Int = 86400000 * 7
    }

    private fun getUserId(claim: Claims): Int {
        try {
            return claim.get("userId", Int::class.javaObjectType)
        } catch(e: Exception) {
            throw AuthenticateException("JWT Claim에 userId가 없습니다.")
        }
    }

    private fun extractExp(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    private fun extractAllClaims(token: String) : Claims {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
        } catch(expiredJwtException: ExpiredJwtException) {
            throw AuthenticateException("Jwt 토큰이 만료되었습니다.")
        } catch(unsupportedJwtException: UnsupportedJwtException) {
            throw AuthenticateException("지원되지 않는 Jwt 토큰입니다.")
        } catch(malformedJwtException: MalformedJwtException) {
            throw AuthenticateException("잘못된 형식의 Jwt 토큰입니다.")
        } catch(signatureException: SignatureException) {
            throw AuthenticateException("Jwt Signature이 잘못된 값입니다.")
        } catch(illegalArgumentException: IllegalArgumentException) {
            throw AuthenticateException("Jwt 헤더 값이 잘못되었습니다.")
        }
    }

    private fun createToken(claims: Map<String, Any>, exp: Int): String {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + exp))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun <T> extractClaim(token: String, claimResolver: Function<Claims, T>): T {
        return claimResolver.apply(extractAllClaims(token))
    }

    fun extractUserId(token: String): Int {
        return extractClaim(token, this::getUserId)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExp(token).before(Date())
    }

    fun generateAccessToken(userId: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = userId
        return createToken(claims, ACCESS_TOKEN_EXP)
    }

    fun generateRefreshToken(userId: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = userId
        return createToken(claims, REFRESH_TOKEN_EXP)
    }

    fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val userDetails = UserDetailsImpl(extractUserId(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }
}
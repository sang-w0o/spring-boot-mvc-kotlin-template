package com.template.auth.service

import com.template.auth.dto.AccessTokenUpdateRequestDto
import com.template.auth.dto.AccessTokenUpdateResponseDto
import com.template.auth.dto.LoginRequestDto
import com.template.auth.dto.LoginResponseDto
import com.template.auth.exception.AuthenticateException
import com.template.auth.exception.LoginException
import com.template.auth.tools.JwtTokenUtil
import com.template.domain.user.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder
) {

    @Transactional(readOnly = true)
    fun updateAccessToken(dto: AccessTokenUpdateRequestDto): AccessTokenUpdateResponseDto {
        if (!jwtTokenUtil.isTokenExpired(dto.refreshToken)) {
            val userId = jwtTokenUtil.extractUserId(dto.refreshToken)
            if (userRepository.existsById(userId)) {
                return AccessTokenUpdateResponseDto(jwtTokenUtil.generateAccessToken(userId))
            } else throw AuthenticateException("Unauthorized User Id.")
        } else throw AuthenticateException("RefreshToken has been expired.")
    }

    @Transactional(readOnly = true)
    fun login(requestDto: LoginRequestDto): LoginResponseDto {
        val user = userRepository.findByEmail(requestDto.email).orElseThrow { LoginException() }
        if (!encoder.matches(requestDto.password, user.password)) throw LoginException()
        return LoginResponseDto(
            accessToken = jwtTokenUtil.generateAccessToken(user.id!!),
            refreshToken = jwtTokenUtil.generateAccessToken(user.id),
        )
    }
}

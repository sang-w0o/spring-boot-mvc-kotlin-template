package com.template.auth.service

import com.template.auth.dto.AccessTokenUpdateRequestDto
import com.template.auth.dto.AccessTokenUpdateResponseDto
import com.template.auth.exception.AuthenticateException
import com.template.auth.tools.JwtTokenUtil
import com.template.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(private val jwtTokenUtil: JwtTokenUtil, private val userRepository: UserRepository) {

    @Transactional(readOnly = true)
    fun updateAccessToken(dto: AccessTokenUpdateRequestDto): AccessTokenUpdateResponseDto {
        if (!jwtTokenUtil.isTokenExpired(dto.refreshToken)) {
            val userId = jwtTokenUtil.extractUserId(dto.refreshToken)
            if(userRepository.existsById(userId)) {
                return AccessTokenUpdateResponseDto(jwtTokenUtil.generateAccessToken(userId))
            } else throw AuthenticateException("Unauthorized User Id.")
        } else throw AuthenticateException("RefreshToken has been expired.")
    }
}
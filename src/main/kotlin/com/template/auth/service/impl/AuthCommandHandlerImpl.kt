package com.template.auth.service.impl

import com.template.auth.exception.AuthenticateException
import com.template.auth.exception.LoginException
import com.template.auth.service.AuthCommandHandler
import com.template.auth.tools.JwtTokenUtil
import com.template.user.domain.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthCommandHandlerImpl(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userRepository: UserRepository,
    private val encoder: BCryptPasswordEncoder
) : AuthCommandHandler {
    @Transactional(readOnly = true)
    override fun updateAccessToken(refreshToken: String): String {
        if (!jwtTokenUtil.isTokenExpired(refreshToken)) {
            val userId = jwtTokenUtil.extractUserId(refreshToken)
            if (userRepository.existsById(userId)) {
                return jwtTokenUtil.generateAccessToken(userId)
            } else throw AuthenticateException("Unauthorized User Id.")
        } else throw AuthenticateException("RefreshToken has been expired.")
    }

    @Transactional(readOnly = true)
    override fun login(email: String, password: String): Pair<String, String> {
        val user = userRepository.findByEmail(email).orElseThrow { LoginException() }
        if (!encoder.matches(password, user.password)) throw LoginException()
        val accessToken = jwtTokenUtil.generateAccessToken(user.id!!)
        val refreshToken = jwtTokenUtil.generateAccessToken(user.id!!)
        return Pair(accessToken, refreshToken)
    }
}

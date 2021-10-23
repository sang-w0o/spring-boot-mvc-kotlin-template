package com.template.unit

import com.template.auth.tools.JwtProperties
import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.util.TestUtils.EMAIL
import com.template.util.TestUtils.EXTRA_TIME
import com.template.util.TestUtils.JWT_ACCESS_TOKEN_EXP
import com.template.util.TestUtils.JWT_REFRESH_TOKEN_EXP
import com.template.util.TestUtils.JWT_SECRET
import com.template.util.TestUtils.NAME
import com.template.util.TestUtils.PASSWORD
import com.template.util.TestUtils.USER_ID
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(JwtProperties::class)
@ContextConfiguration(initializers = [ConfigDataApplicationContextInitializer::class])
abstract class BaseUnitTest {

    @MockBean
    protected lateinit var userRepository: UserRepository

    protected var jwtProperties: JwtProperties = JwtProperties()

    init {
        jwtProperties.secret = JWT_SECRET
        jwtProperties.accessTokenExp = JWT_ACCESS_TOKEN_EXP
        jwtProperties.refreshTokenExp = JWT_REFRESH_TOKEN_EXP
    }

    protected fun getMockUser(): User {
        val savedUser = User(NAME, EMAIL, PASSWORD)
        savedUser.id = USER_ID
        return savedUser
    }

    protected fun generateTokenWithoutUserIdClaim(): String {
        return Jwts.builder()
            .setClaims(mutableMapOf())
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + EXTRA_TIME))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret)
            .compact()
    }
}

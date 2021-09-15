package com.template.unit.auth

import com.template.auth.tools.JwtProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(JwtProperties::class)
@ContextConfiguration(initializers = [ConfigDataApplicationContextInitializer::class])
@ActiveProfiles("test")
abstract class BaseUnitTest {

    companion object {
        const val EMAIL = "test@test.com"
        const val NAME = "testUserName"
        const val PASSWORD = "testPassword"
        const val USER_ID = 1
        const val TOKEN = "token"
    }

    protected lateinit var encoder: BCryptPasswordEncoder

    @BeforeEach
    fun setUpBaseTest() {
        encoder = BCryptPasswordEncoder(10)
    }
}

package com.template.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.template.auth.exception.UserIdNotFoundException
import com.template.auth.tools.JwtTokenUtil
import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.util.TestUtils.EMAIL
import com.template.util.TestUtils.NAME
import com.template.util.TestUtils.PASSWORD
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class ApiIntegrationTest {

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    protected lateinit var encoder: BCryptPasswordEncoder

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        val user = User(
            email = EMAIL,
            name = NAME,
            password = encoder.encode(PASSWORD)
        )
        userRepository.save(user)
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    protected fun getUserId(): Int {
        val user = userRepository.findByEmail(EMAIL).orElseThrow { UserIdNotFoundException() }
        return user.id ?: -1
    }

    protected fun assertErrorResponse(dsl: MockMvcResultMatchersDsl, message: String) {
        dsl.jsonPath("timestamp") { exists() }
        dsl.jsonPath("status") { exists() }
        dsl.jsonPath("error") { exists() }
        dsl.jsonPath("message") { value(message) }
        dsl.jsonPath("path") { exists() }
        dsl.jsonPath("remote") { exists() }
    }
}

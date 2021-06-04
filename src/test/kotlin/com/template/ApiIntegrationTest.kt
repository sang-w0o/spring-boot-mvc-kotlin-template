package com.template

import com.template.auth.exception.UserIdNotFoundException
import com.template.auth.tools.JwtTokenUtil
import com.template.domain.user.User
import com.template.domain.user.UserRepository
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = ["classpath:application-test.properties"])
abstract class ApiIntegrationTest {

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    protected lateinit var encoder: BCryptPasswordEncoder

    companion object {
        const val EMAIL = "test@test.com"
        const val NAME  = "testUserName"
        const val PASSWORD = "testPassword"
    }

    @Before
    fun setUp() {
        val user = User(
            email = EMAIL,
            name = NAME,
            password = encoder.encode(PASSWORD))
        val a = userRepository.save(user)
    }

    @After
    fun tearDown() {
        userRepository.deleteAll()
    }

    protected fun getUserId(): Int {
        val user = userRepository.findByEmail(EMAIL).orElseThrow{ UserIdNotFoundException() }
        val userId = user.id
        if(userId != null) return userId
        else return -1
    }

    protected fun getUser(): User {
        return userRepository.findByEmail(EMAIL).orElseThrow { UserIdNotFoundException() }
    }

    protected fun assertResponseStatus(httpStatus: HttpStatus, requestEntity: RequestEntity<*>) {
        val responseEntity = restTemplate.exchange(requestEntity, String::class.java)
        assertEquals(httpStatus, responseEntity.statusCode)
    }

    protected fun saveUser(email: String, name: String, password: String) {
        val user = User(email, name, password)
        userRepository.save(user)
    }
}
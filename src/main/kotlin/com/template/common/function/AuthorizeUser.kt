package com.template.common.function

import com.template.domain.user.User
import com.template.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class AuthorizeUser : Function<Int, User> {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun apply(userId: Int): User {
        return userRepository.findById(userId).orElseThrow { UsernameNotFoundException("잘못된 userId 값입니다.") }
    }
}
package com.template.common.function

import com.template.auth.exception.UserUnAuthorizedException
import com.template.domain.user.User
import com.template.domain.user.UserRepository
import com.template.security.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class FindUser : Supplier<User> {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun get(): User {
        val userId = Integer.parseInt((SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).username)
        return userRepository.findById(userId).orElseThrow { UserUnAuthorizedException() }

    }
}
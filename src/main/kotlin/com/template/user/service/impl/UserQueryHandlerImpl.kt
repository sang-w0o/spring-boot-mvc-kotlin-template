package com.template.user.service.impl

import com.template.user.domain.UserRepository
import com.template.user.service.UserQueryHandler
import com.template.user.service.dto.UserDto
import org.springframework.stereotype.Service

@Service
class UserQueryHandlerImpl(
    private val userRepository: UserRepository
) : UserQueryHandler {
    override fun getUserInfo(userDto: UserDto) = userDto
}

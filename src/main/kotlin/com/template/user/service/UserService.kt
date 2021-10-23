package com.template.user.service

import com.template.user.domain.UserRepository
import com.template.user.dto.UserDto
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserInfo(userDto: UserDto) = userDto.toResponseDto()
}

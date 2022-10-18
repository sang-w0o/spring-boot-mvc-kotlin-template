package com.template.user.service

import com.template.user.service.dto.UserDto

interface UserQueryHandler {
    fun getUserInfo(userDto: UserDto): UserDto
}

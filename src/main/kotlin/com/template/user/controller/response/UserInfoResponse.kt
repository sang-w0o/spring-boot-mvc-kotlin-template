package com.template.user.controller.response

import com.template.user.service.dto.UserDto

data class UserInfoResponse(
    val id: Int,
    val name: String,
    val email: String
) {
    companion object {
        fun from(userDto: UserDto) = UserInfoResponse(userDto.id, userDto.name, userDto.email)
    }
}

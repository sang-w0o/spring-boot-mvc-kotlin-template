package com.template.user.dto

import com.template.user.domain.User

data class UserDto(
    val id: Int,
    val name: String,
    val password: String,
    val email: String
) {
    constructor(user: User) : this(user.id!!, user.name, user.password!!, user.email)

    fun toResponseDto() = UserInfoResponseDto(this.id, this.name, this.email)
}

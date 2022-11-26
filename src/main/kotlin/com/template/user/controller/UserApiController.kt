package com.template.user.controller

import com.template.config.annotation.LoggedInUser
import com.template.user.controller.response.UserInfoResponse
import com.template.user.service.UserQueryHandler
import com.template.user.service.dto.UserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/user/")
class UserApiController(
    private val userQueryHandler: UserQueryHandler
) {

    @GetMapping
    fun getUserInfo(@LoggedInUser user: UserDto) = UserInfoResponse.from(userQueryHandler.getUserInfo(user))
}

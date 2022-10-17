package com.template.user.controller

import com.template.config.annotation.LoggedInUser
import com.template.user.controller.response.UserInfoResponse
import com.template.user.service.UserService
import com.template.user.service.dto.UserDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/user/")
class UserApiController(
    private val userService: UserService
) {

    @GetMapping
    fun getUserInfo(@LoggedInUser user: UserDto) = UserInfoResponse.from(userService.getUserInfo(user))
}

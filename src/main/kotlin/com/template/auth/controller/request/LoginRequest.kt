package com.template.auth.controller.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class LoginRequest(
    @field:Email
    @field:NotBlank(message = "Email is required.")
    var email: String = "",

    @field:NotBlank(message = "Password is required.")
    @field:Size(min = 5, max = 72)
    var password: String = ""
)

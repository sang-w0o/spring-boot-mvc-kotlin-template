package com.template.config.handler

import com.template.config.annotation.LoggedInUser
import com.template.user.service.dto.UserDto
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserInfoArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(LoggedInUser::class.java) != null && parameter.parameterType == UserDto::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        if (authentication.principal is UserDto) {
            return authentication.principal as UserDto
        } else throw AssertionError("Authentication.principal is not type of UserDto")
    }
}

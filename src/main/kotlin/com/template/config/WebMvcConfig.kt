package com.template.config

import com.template.config.handler.UserInfoArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val userInfoArgumentResolver: UserInfoArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userInfoArgumentResolver)
    }
}

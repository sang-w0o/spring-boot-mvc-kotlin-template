package com.template.auth.tools

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@Component
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    var secret: String = ""
    var accessTokenExp: Int = 0
    var refreshTokenExp: Int = 0
}

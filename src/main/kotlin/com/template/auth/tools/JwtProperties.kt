package com.template.auth.tools

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    var secret: String = ""
    var accessTokenExp: Int = 0
    var refreshTokenExp: Int = 0
}

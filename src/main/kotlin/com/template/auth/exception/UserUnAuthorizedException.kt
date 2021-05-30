package com.template.auth.exception

import com.template.common.exception.UnauthorizedException

class UserUnAuthorizedException : UnauthorizedException {
    constructor(message: String): super(message)
    constructor(): super("인증되지 않은 사용자 입니다.")
}
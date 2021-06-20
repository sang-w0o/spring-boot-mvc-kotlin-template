package com.template.auth.exception

import com.template.common.exception.NotFoundException

class LoginException: NotFoundException {
    constructor(): super("이메일 또는 비밀번호가 잘못되었습니다.")
}
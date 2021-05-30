package com.template.auth.exception

import com.template.common.exception.NotFoundException

class UserIdNotFoundException: NotFoundException {
    constructor(message: String): super(message)
    constructor(): super("userId is invalid.")
}
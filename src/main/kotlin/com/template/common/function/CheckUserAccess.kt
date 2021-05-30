package com.template.common.function

import com.template.auth.exception.InvalidAccessException
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class CheckUserAccess(private val findUser: FindUser) : Consumer<Int> {
    override fun accept(userId: Int) {
        val user = findUser.get()
        if(user.id!! != userId) throw InvalidAccessException()
    }
}
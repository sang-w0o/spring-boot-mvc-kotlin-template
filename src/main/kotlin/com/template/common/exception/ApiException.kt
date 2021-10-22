package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

abstract class ApiException(message: String, status: HttpStatus) : ResponseStatusException(status, message) {
    override val message: String
        get() = reason ?: "No message provided."
}

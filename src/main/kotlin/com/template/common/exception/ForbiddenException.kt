package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
abstract class ForbiddenException(message: String) : ApiException(message, HttpStatus.FORBIDDEN)

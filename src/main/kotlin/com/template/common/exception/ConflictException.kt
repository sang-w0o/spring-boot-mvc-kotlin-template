package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
abstract class ConflictException(message: String) : ApiException(message, HttpStatus.CONFLICT)

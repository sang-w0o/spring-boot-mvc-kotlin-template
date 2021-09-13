package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
abstract class BadRequestException(message: String) : ApiException(message, HttpStatus.BAD_REQUEST)

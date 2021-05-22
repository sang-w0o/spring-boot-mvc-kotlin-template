package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
abstract class NotAcceptableException(message: String) : ApiException(message, HttpStatus.NOT_ACCEPTABLE)
package com.template.common.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

abstract class ApiException(message: String, val httpStatus: HttpStatus) : RuntimeException()
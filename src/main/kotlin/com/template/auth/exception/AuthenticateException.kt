package com.template.auth.exception

import org.springframework.security.core.AuthenticationException

class AuthenticateException(message: String) : AuthenticationException(message)

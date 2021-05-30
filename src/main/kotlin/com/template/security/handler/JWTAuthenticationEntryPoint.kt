package com.template.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.template.common.dto.ErrorResponseDto
import com.template.common.tools.DateConverter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JWTAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        request!!
        val errorResponseDto = ErrorResponseDto(
            DateConverter.convertDateWithTime(LocalDateTime.now()), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase, exception?.message!!, request.requestURI, request.remoteAddr)
        response?.status = HttpStatus.UNAUTHORIZED.value()
        response?.contentType = MediaType.APPLICATION_JSON_VALUE
        response?.characterEncoding = "UTF-8"
        response?.writer?.println(convertObjectToJson(errorResponseDto))
    }

    private fun convertObjectToJson(obj: Any): String? {
        if (obj == null) {
            return null;
        }
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(obj);
    }
}
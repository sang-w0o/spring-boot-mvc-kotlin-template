package com.template.config

import com.template.auth.exception.AuthenticateException
import com.template.common.dto.ErrorResponseDto
import com.template.common.exception.ApiException
import com.template.common.tools.DateConverter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Exception::class])
    protected fun handleApiException(exception: Exception, request: WebRequest): ResponseEntity<Any> {
        return when (exception) {
            is ApiException -> {
                handleExceptionInternal(exception, null, HttpHeaders(), exception.httpStatus, request)
            }
            is AuthenticateException -> {
                handleExceptionInternal(exception, null, HttpHeaders(), HttpStatus.UNAUTHORIZED, request)
            }
            else -> handleExceptionInternal(exception, null, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)
        }
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val servletWebRequest = request as ServletWebRequest
        val errorResponseDto = ErrorResponseDto(DateConverter.convertDateWithTime(LocalDateTime.now()), status.value(), status.reasonPhrase, ex.bindingResult.fieldErrors[0].defaultMessage, servletWebRequest.request.requestURI, servletWebRequest.request.remoteAddr)
        return ResponseEntity(errorResponseDto, headers, status)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorResponseDto: ErrorResponseDto
        val servletWebRequest = request as ServletWebRequest
        errorResponseDto = if(status == HttpStatus.INTERNAL_SERVER_ERROR) {
            ErrorResponseDto(DateConverter.convertDateWithTime(LocalDateTime.now()), status.value(), status.reasonPhrase, "Internal Server Error", servletWebRequest.request.requestURI, servletWebRequest.request.remoteAddr)
        } else {
            ErrorResponseDto(DateConverter.convertDateWithTime(LocalDateTime.now()), status.value(), status.reasonPhrase, ex.message!!, servletWebRequest.request.requestURI, servletWebRequest.request.remoteAddr)
        }
        return ResponseEntity(errorResponseDto, headers, status)
    }

}
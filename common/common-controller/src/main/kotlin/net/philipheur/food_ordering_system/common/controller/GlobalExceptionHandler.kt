package net.philipheur.food_ordering_system.common.controller

import jakarta.validation.ConstraintViolationException
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = logger()

    @ResponseBody
    @ExceptionHandler(value = [Exception::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception): ErrorDto {
        log.error(exception.message, exception)
        return ErrorDto(
            code = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Unexpected error"
        )
    }

    @ResponseBody
    @ExceptionHandler(value = [ValidationException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(exception: ValidationException): ErrorDto {
        val errorDTO: ErrorDto
        if (exception is ConstraintViolationException) {
            val violations = extractViolationsFromException(exception)
            log.error(violations, exception)
            errorDTO = ErrorDto(
                code = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = violations
            )
        } else {
            log.error(exception.message)
            errorDTO = ErrorDto(
                code = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = exception.message ?: ""
            )
        }

        return errorDTO
    }

    private fun extractViolationsFromException(
        exception: ConstraintViolationException
    ): String {
        return exception.constraintViolations
            .joinToString("--") { it.message }
    }
}
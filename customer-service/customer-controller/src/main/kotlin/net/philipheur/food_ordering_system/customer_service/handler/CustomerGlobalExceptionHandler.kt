package net.philipheur.food_ordering_system.customer_service.handler

import net.philipheur.food_ordering_system.common.controller.ErrorDto
import net.philipheur.food_ordering_system.common.controller.GlobalExceptionHandler
import net.philipheur.food_ordering_system.customer_service.domain.core.exception.CustomerDomainException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

class CustomerGlobalExceptionHandler: GlobalExceptionHandler() {

    @ResponseBody
    @ExceptionHandler(
        value = [
            CustomerDomainException::class
        ]
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(
        customerDomainException: CustomerDomainException
    ): ErrorDto {

        log.error(
            customerDomainException.message,
            customerDomainException
        )

        return ErrorDto(
            code = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = customerDomainException.message!!
        )
    }

}
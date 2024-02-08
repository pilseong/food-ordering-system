package net.philipheur.food_ordering_system.order_service.rest.exception.handler

import net.philipheur.food_ordering_system.common.controller.ErrorDto
import net.philipheur.food_ordering_system.common.controller.GlobalExceptionHandler
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderDomainException
import net.philipheur.food_ordering_system.order_service.domain.core.exception.OrderNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus


@ControllerAdvice
class OrderGlobalExceptionHandler
    : GlobalExceptionHandler() {

    @ResponseBody
    @ExceptionHandler(
        value = [
            OrderDomainException::class
        ]
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(
        orderDomainException: OrderDomainException
    ): ErrorDto {

        log.error(
            orderDomainException.message,
            orderDomainException
        )

        return ErrorDto(
            code = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = orderDomainException.message!!
        )
    }

    @ResponseBody
    @ExceptionHandler(
        value = [
            OrderNotFoundException::class
        ]
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleException(
        orderNotFoundException: OrderNotFoundException
    ): ErrorDto {

        log.error(
            orderNotFoundException.message,
            orderNotFoundException
        )

        return ErrorDto(
            code = HttpStatus.NOT_FOUND.reasonPhrase,
            message = orderNotFoundException.message!!
        )
    }
}
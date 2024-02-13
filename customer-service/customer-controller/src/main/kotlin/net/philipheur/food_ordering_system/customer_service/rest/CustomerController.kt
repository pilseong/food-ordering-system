package net.philipheur.food_ordering_system.customer_service.rest

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerCommand
import net.philipheur.food_ordering_system.customer_service.domain.application_service.dto.create.CreateCustomerResponseDto
import net.philipheur.food_ordering_system.customer_service.domain.application_service.ports.input.service.CustomerApplicationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(
    value = ["/customers"],
//    produces = ["application/vnd.api.v1+json"]
)
class CustomerController(
    private val service: CustomerApplicationService
) {
    private val log by LoggerDelegator()

    @PostMapping
    fun createOrder(
        @RequestBody command: CreateCustomerCommand
    ): ResponseEntity<CreateCustomerResponseDto> {
        log.info(
            "Creating customer for customer username: ${command.username}"
        )

        val createCustomerResponseDto = service.createCustomer(command)
        log.info(
            "Customer created with " +
                    "username: ${command.username}"
        )

        return ResponseEntity.ok(createCustomerResponseDto)
    }
}
package net.philipheur.food_ordering_system.order_service.rest

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderResponseDto
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.service.OrderApplicationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

//inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

@RestController
@RequestMapping(
    value = ["/orders"],
//    produces = ["application/vnd.api.v1+json"]
)
class OrderController(
    private val orderApplicationService: OrderApplicationService
) {
    private val log by LoggerDelegator()

    @PostMapping
    fun createOrder(
        @RequestBody createOrderCommand: CreateOrderCommand
    ): ResponseEntity<CreateOrderResponseDto> {
        log.info(
            "Creating order for customer: {} at restaurant: {}",
            createOrderCommand.customerId, createOrderCommand.restaurantId
        )

        val createOrderResponse = orderApplicationService.createOrder(createOrderCommand)
        log.info(
            "Order created with " +
                    "tracking id: ${createOrderResponse.orderTrackingId}"
        )

        return ResponseEntity.ok(createOrderResponse)
    }

    @GetMapping("/{trackingId}")
    fun getOrderByTrackingInfo(
        @PathVariable trackingId: UUID
    ): ResponseEntity<TrackOrderResponse> {
        val trackOrderResponse = orderApplicationService
            .trackOrder(
                command = TrackOrderCommand(
                    orderTrackingId = trackingId
                )
            )

        log.info(
            "Return order status with " +
                    "tracking id: $trackOrderResponse.orderTrackingId}"
        )

        return ResponseEntity.ok(trackOrderResponse)
    }
}
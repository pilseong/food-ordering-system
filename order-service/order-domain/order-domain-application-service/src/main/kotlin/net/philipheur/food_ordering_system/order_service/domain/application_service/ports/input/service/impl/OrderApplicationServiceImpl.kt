package net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.service.impl

import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.create.CreateOrderResponseDto
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderCommand
import net.philipheur.food_ordering_system.order_service.domain.application_service.dto.track.TrackOrderResponse
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.OrderCreateHelper
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.TrackOrderHelper
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.input.service.`interface`.OrderApplicationService
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import net.philipheur.food_ordering_system.order_service.domain.core.logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

@Service
class OrderApplicationServiceImpl(
    private val orderCreateHelper: OrderCreateHelper,
    private val trackOrderHelper: TrackOrderHelper,
    private val orderCreatedPaymentRequestMessagePublisher:
    OrderCreatedPaymentRequestMessagePublisher,
) : OrderApplicationService {

    private val log = logger()

    override fun createOrder(
        command: CreateOrderCommand,
    ): CreateOrderResponseDto {

        val event = orderCreateHelper
            .persistOrder(command)

        log.info(
            "Order is created with " +
                    "id: ${event.order.id!!.value}"
        )

        // 생성 종료를 알리는 이벤트를 발송한다.
        orderCreatedPaymentRequestMessagePublisher.publish(event)

        // 결과 반환
        return CreateOrderResponseDto(
            orderTrackingId = event.order.trackingId!!.value,
            orderStatus = event.order.orderStatus!!,
            message = "Order Created Successfully"
        )
    }

    override fun trackOrder(
        command: TrackOrderCommand
    ): TrackOrderResponse {

        val order = trackOrderHelper.trackOrder(command)

        return TrackOrderResponse(
            orderTrackingId = order.trackingId!!.value,
            orderStatus = order.orderStatus!!,
            message = "Order Created Successfully"
        )
    }
}
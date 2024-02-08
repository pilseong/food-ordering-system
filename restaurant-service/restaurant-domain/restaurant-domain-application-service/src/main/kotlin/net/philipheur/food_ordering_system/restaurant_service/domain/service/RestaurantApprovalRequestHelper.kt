package net.philipheur.food_ordering_system.restaurant_service.domain.service

import net.philipheur.food_ordering_system.common.domain.valueobject.*
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.order.SagaConstants
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalEventPayload
import net.philipheur.food_ordering_system.payment_service.domain.service.outbox.model.OrderApprovalOutboxMessage
import net.philipheur.food_ordering_system.restaurant_service.domain.core.RestaurantDomainService
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.OrderDetail
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Product
import net.philipheur.food_ordering_system.restaurant_service.domain.core.entity.Restaurant
import net.philipheur.food_ordering_system.restaurant_service.domain.core.event.OrderApprovalEvent
import net.philipheur.food_ordering_system.restaurant_service.domain.core.exception.RestaurantNotFoundException
import net.philipheur.food_ordering_system.restaurant_service.domain.service.dto.RestaurantApprovalRequest
import net.philipheur.food_ordering_system.restaurant_service.domain.service.outbox.scheduler.OrderApprovalOutboxHelper
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher.OrderApprovedMessagePublisher
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.publisher.OrderRejectedMessagePublisher
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.OrderApprovalRepository
import net.philipheur.food_ordering_system.restaurant_service.domain.service.ports.output.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Component
open class RestaurantApprovalRequestHelper(
    private val restaurantDomainService: RestaurantDomainService,
    private val orderApprovalOutboxHelper: OrderApprovalOutboxHelper,
    private val restaurantRepository: RestaurantRepository,
    private val orderApprovalRepository: OrderApprovalRepository,
    private val orderApprovedMessagePublisher: OrderApprovedMessagePublisher,
    private val orderRejectedMessagePublisher: OrderRejectedMessagePublisher
) {
    private val log by LoggerDelegator()

    @Transactional
    open fun persistOrderApproval(
        approvalRequest: RestaurantApprovalRequest
    ) {

        // outbox 메시지가 저장되어 있으면 이미 처리가 된 요청이다.
        if (checkIfAlreadySaved(approvalRequest)) {
            log.info(
                "An outbox message with saga id: " +
                        "${approvalRequest.sagaId} is already saved to database!"
            )
            return
        }

        log.info(
            "Processing restaurant approval for " +
                    "order id: ${approvalRequest.orderId} "
        )

        val failureMessages = mutableListOf<String>()

        // 식당 검색 하여 정보를 채운다
        val restaurant = findRestaurant(approvalRequest)

        // 주문 정보를 검증한다.
        val orderApprovalEvent = restaurantDomainService.validateOrder(
            restaurant = restaurant,
            failureMessages = failureMessages,
            orderApprovedEventDomainEventPublisher = orderApprovedMessagePublisher,
            orderRejectedEventDomainEventPublisher = orderRejectedMessagePublisher
        )

        // 주문 처리 결과를 저장
        orderApprovalRepository.save(restaurant.orderApproval!!)


        // outbox 메시지를 저장한다.
        saveNewOrderApprovalOutboxMessage(
            event = orderApprovalEvent,
            approvalRequest = approvalRequest,
        )
    }


    // 새로운 order outbox message 를 저장한다.
    private fun saveNewOrderApprovalOutboxMessage(
        event: OrderApprovalEvent,
        approvalRequest: RestaurantApprovalRequest
    ) {
        // order outbox 메시지 생성
        val payload = OrderApprovalEventPayload(
            restaurantId = event.orderApproval.restaurantId.value.toString(),
            orderId = event.orderApproval.orderId.value.toString(),
            createdAt = event.createdAt,
            orderApprovalStatus = event.orderApproval.orderApprovalStatus.name,
            failureMessages = event.failureMessages,
        )

        // order outbox message 저장
        orderApprovalOutboxHelper.saveOutboxMessage(
            OrderApprovalOutboxMessage(
                id = UUID.randomUUID(),
                sagaId = approvalRequest.sagaId,
                createdAt = payload.createdAt,
                processedAt = ZonedDateTime.now(ZoneId.of(DomainConstant.UTC)),
                type = SagaConstants.ORDER_SAGA_NAME,
                payload = orderApprovalOutboxHelper.createPayload(payload),
                orderApprovalStatus = event.orderApproval.orderApprovalStatus,
                outboxStatus = OutboxStatus.STARTED
            )
        )
    }


    private fun checkIfAlreadySaved(
        approvalRequest: RestaurantApprovalRequest,
    ): Boolean {
        log.info("checkIfAlreadySaved with data $approvalRequest")

        // 같은 상태에 메시지가 있는지 확인
        // 이미 메시지가 저장되어 있으면 중복 메시지이므로 무시한다. 저장된 메시는 스케줄러에서 발송
        // 중복이면 true
        return orderApprovalOutboxHelper.getPaymentOutboxMessageBySagaIdAndOrderApprovalStatusAndOutboxStatus(
            sagaId = approvalRequest.sagaId,
            approvalStatus = OrderApprovalStatus.APPROVED,
            outboxStatus = OutboxStatus.STARTED,
        ) != null
    }

    private fun findRestaurant(
        restaurantApprovalRequest: RestaurantApprovalRequest
    ): Restaurant {

        // 요청 객체 정보를 가지고 식당 객체를 생성
        val restaurant = Restaurant(
            restaurantId = RestaurantId(restaurantApprovalRequest.restaurantId),
            orderDetail = OrderDetail(
                orderId = OrderId(restaurantApprovalRequest.orderId),
                products = restaurantApprovalRequest.products.map {
                    Product(
                        productId = it.id!!,
                        quantity = it.quantity,
                    )
                },
                totalAmount = Money(restaurantApprovalRequest.price),
                orderStatus = OrderStatus.valueOf(
                    restaurantApprovalRequest.restaurantOrderStatus.name
                )
            )
        )

        // 식당 정보를 데이터베이스에서 검색
        val restaurantEntity = restaurantRepository
            .findRestaurantInformation(restaurant)

        if (restaurantEntity == null) {
            log.error("Restaurant id: ${restaurant.id} not found")
            throw RestaurantNotFoundException(
                "Restaurant with " +
                        "id ${restaurant.id} not found"
            )
        }

        // 읽어온 식당 정보를 식당 객체에 업데이트 한다.ㅇ
        restaurant.active = restaurantEntity.active
        restaurant.orderDetail.products.forEach { product ->
            restaurantEntity.orderDetail.products.forEach { entity ->
                if (product.id == entity.id) {
                    product.updateWithConfirmedNamePriceAndAvailability(
                        entity.name!!,
                        entity.price!!,
                        entity.available!!
                    )
                }
            }
        }

        return restaurant
    }
}
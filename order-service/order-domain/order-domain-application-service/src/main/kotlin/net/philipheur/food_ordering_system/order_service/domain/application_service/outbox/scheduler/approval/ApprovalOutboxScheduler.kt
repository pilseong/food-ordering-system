package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.approval

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxScheduler
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class ApprovalOutboxScheduler(
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val approvalRequestMessagePublisher: RestaurantApprovalRequestMessagePublisher
) : OutboxScheduler {

    private val log by LoggerDelegator()

    @Transactional
    @Scheduled(
        fixedDelayString = "\${order-service.outbox-scheduler-fixed-rate}",
        initialDelayString = "\${order-service.outbox-scheduler-initial-delay}"
    )
    override fun processOutboxMessage() {
        // 메시지 검색
        val approvalOutboxMessages =
            approvalOutboxHelper
                .getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                    outboxStatus = OutboxStatus.STARTED,
                    sagaStatus = arrayOf(SagaStatus.PROCESSING)
                )

        // 메시지 처리
        if (approvalOutboxMessages.isNotEmpty()) {
            log.info(
                "Received ${approvalOutboxMessages.size} OrderApprovalOutboxMessages with " +
                        "ids: ${
                            approvalOutboxMessages.joinToString(",") { it.payload }
                        }, sending to message bus!",
            )

            // 메시지 발송
            approvalOutboxMessages
                .forEach { message ->

                    // 콜백 등록 및 발송
                    approvalRequestMessagePublisher
                        .publish(message) { originalMessage, status ->
                            // 콜백 내에서 결과를 수신하여 outbox 데이터베이스 상태를 업데이트 한다.
                            val updatedMessage = originalMessage.copy(
                                outboxStatus = status
                            )

                            approvalOutboxHelper
                                .saveOutboxMessage(updatedMessage);

                            log.info(
                                "OrderApprovalOutboxMessages is updated with " +
                                        "outbox status: ${status.name}"
                            );
                        }
                }

            log.info(
                "${approvalOutboxMessages.size} OrderApprovalOutboxMessages" +
                        " sent to message bus!"
            )
        }
    }
}
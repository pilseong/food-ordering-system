package net.philipheur.food_ordering_system.payment_service.domain.service.outbox.scheduler

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxScheduler
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.payment_service.domain.service.ports.output.message.publisher.PaymentResponseMessagePublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
open class OrderOutboxScheduler(
    private val orderOutboxHelper: OrderOutboxHelper,
    private val paymentResponseMessagePublisher: PaymentResponseMessagePublisher,
) : OutboxScheduler {
    private val log by LoggerDelegator()

    @Transactional
    @Scheduled(
        fixedDelayString = "\${payment-service.outbox-scheduler-fixed-rate}",
        initialDelayString = "\${payment-service.outbox-scheduler-initial-delay}"
    )
    // 주기적으로 돌아가면서 데이터 베이스에 쌓아였는 메시지를 발송한다.
    override fun processOutboxMessage() {

        // 처리할 메시지 데이터 베이스에서 검색
        val outboxMessages =
            orderOutboxHelper
                .getOrderOutboxMessageByOutboxStatus(
                    outboxStatus = OutboxStatus.STARTED,
                )
        // 메시지 처리 시작
        if (outboxMessages.isNotEmpty()) {
            log.info(
                "Received ${outboxMessages.size} OrderOutboxMessages sending to message bus.\n " +
                        "with ids: ${
                            outboxMessages.map { it.id }.joinToString(",")
                        }",
            )

            // 메시지 발송
            outboxMessages
                .forEach { message ->
                    // 콜백 등록 및 발송
                    paymentResponseMessagePublisher
                        .publish(message) { originalMessage, status ->
                            // 콜백 내에서 결과를 수신하여 outbox 데이터베이스 상태를 업데이트 한다.
                            val updatedMessage = originalMessage.copy(
                                outboxStatus = status
                            )

                            orderOutboxHelper
                                .saveOutboxMessage(updatedMessage);

                            log.info(
                                "[Finished]OrderOutboxMessage is updated with " +
                                        "outbox status: ${status.name}"
                            );
                        }
                }

            log.info(
                "${outboxMessages.size} OrderOutboxMessage" +
                        " sent to message bus"
            )
        }
    }
}
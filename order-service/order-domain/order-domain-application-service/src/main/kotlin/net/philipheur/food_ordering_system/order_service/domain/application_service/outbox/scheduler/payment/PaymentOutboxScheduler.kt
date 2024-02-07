package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.payment

import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxScheduler
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus.COMPENSATING
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus.STARTED
import net.philipheur.food_ordering_system.order_service.domain.application_service.ports.output.message.publisher.payment.PaymentRequestMessagePublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
open class PaymentOutboxScheduler(
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val paymentRequestMessagePublisher: PaymentRequestMessagePublisher
) : OutboxScheduler {
    private val log by LoggerDelegator()

    @Transactional
    @Scheduled(
        fixedDelayString = "\${order-service.outbox-scheduler-fixed-rate}",
        initialDelayString = "\${order-service.outbox-scheduler-initial-delay}"
    )
    // 주기적으로 돌아가면서 데이터 베이스에 쌓아였는 메시지를 발송한다.
    override fun processOutboxMessage() {

        // 처리할 메시지 데이터 베이스에서 검색
        val paymentOutboxMessages =
            paymentOutboxHelper
                .getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                    outboxStatus = OutboxStatus.STARTED,
                    sagaStatus = arrayOf(STARTED, COMPENSATING)
                )

        // 메시지 처리 시작
        if (paymentOutboxMessages.isNotEmpty()) {
            log.info(
                "Received ${paymentOutboxMessages.size} OrderPaymentOutboxMessage with " +
                        "ids: ${
                            paymentOutboxMessages.joinToString(",") { it.payload }
                        }, sending to message bus!",
            )

            // 메시지 발송
            paymentOutboxMessages
                .forEach { message ->

                    // 콜백 등록 및 발송
                    paymentRequestMessagePublisher
                        .publish(message) { originalMessage, status ->
                            // 콜백 내에서 결과를 수신하여 outbox 데이터베이스 상태를 업데이트 한다.
                            val updatedMessage = originalMessage.copy(
                                outboxStatus = status
                            )

                            paymentOutboxHelper
                                .saveOutboxMessage(updatedMessage);

                            log.info(
                                "OrderPaymentOutboxMessage is updated with " +
                                        "outbox status: ${status.name}"
                            );
                        }
                }

            log.info(
                "${paymentOutboxMessages.size} OrderPaymentOutboxMessage" +
                        " sent to message bus!"
            )
        }
    }
}
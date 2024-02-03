package net.philipheur.food_ordering_system.order_service.domain.application_service.outbox.scheduler.payment


import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.saga.SagaStatus.*
import net.philipheur.food_ordering_syustem.infrastructure.outbox.OutboxScheduler
import net.philipheur.food_ordering_syustem.infrastructure.outbox.OutboxStatus
import org.springframework.scheduling.annotation.Scheduled


import org.springframework.stereotype.Component

@Component
open class PaymentOutboxCleanerScheduler(
    private val paymentOutboxHelper: PaymentOutboxHelper
) : OutboxScheduler {

    private val log by LoggerDelegator()

    @Scheduled(cron = "@midnight")
    override fun processOutboxMessage() {
        val messages = paymentOutboxHelper
            .getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                outboxStatus = OutboxStatus.COMPLETED,
                sagaStatus = arrayOf(SUCCEEDED, FAILED, COMPENSATED)
            )

        if (messages.isNotEmpty()) {
            log.info(
                "Received ${messages.size} OrderPaymentOutboxMessage for clean up. " +
                        "The payloads: ${messages.joinToString("\n") { it.toString() }}"
            )

            paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                outboxStatus = OutboxStatus.COMPLETED,
                sagaStatus = arrayOf(SUCCEEDED, FAILED, COMPENSATED)
            )

            log.info("${messages.size} OrderPaymentOutboxMessage deleted")
        }
    }
}
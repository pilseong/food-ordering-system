package net.philipheur.food_ordering_system.payment_service.domain.service.outbox.scheduler


import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxScheduler
import net.philipheur.food_ordering_system.infrastructure.outbox.OutboxStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
open class OrderOutboxCleanerScheduler(
    private val orderOutboxHelper: OrderOutboxHelper
) : OutboxScheduler {

    private val log by LoggerDelegator()

    @Scheduled(cron = "@midnight")
    override fun processOutboxMessage() {
        // 삭제할 메시지를 검색 - 처리 완료된 메시지만 가져온다.
        val messages = orderOutboxHelper
            .getOrderOutboxMessageByOutboxStatus(
                outboxStatus = OutboxStatus.COMPLETED,
            )

        if (messages.isNotEmpty()) {
            log.info(
                "Received ${messages.size} OrderOutboxMessage for clean up. " +
                        "The payloads: ${messages.joinToString("\n") { it.toString() }}"
            )

            // 완료 상태 메시지를 삭제한다.
            orderOutboxHelper.deleteOrderOutboxMessageByOutboxStatus(
                outboxStatus = OutboxStatus.COMPLETED,
            )

            log.info("${messages.size} OrderOutboxMessage deleted")
        }
    }
}
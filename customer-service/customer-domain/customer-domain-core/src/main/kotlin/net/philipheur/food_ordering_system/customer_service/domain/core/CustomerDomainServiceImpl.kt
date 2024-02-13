package net.philipheur.food_ordering_system.customer_service.domain.core

import net.philipheur.food_ordering_system.common.domain.valueobject.DomainConstant.Companion.UTC
import net.philipheur.food_ordering_system.common.utils.logging.LoggerDelegator
import net.philipheur.food_ordering_system.customer_service.domain.core.entity.Customer
import net.philipheur.food_ordering_system.customer_service.domain.core.event.CustomerCreatedEvent
import java.time.ZoneId
import java.time.ZonedDateTime

class CustomerDomainServiceImpl
    : CustomerDomainService {

    private val log by LoggerDelegator()

    // 고객 생성 이벤트를 생성하고 돌려 준다. 로직이 없다.
    override fun validateAndInitiateCustomer(customer: Customer): CustomerCreatedEvent {
        log.info("Customer with id: ${customer.id!!.value} is initiated")
        return CustomerCreatedEvent(customer, ZonedDateTime.now(ZoneId.of(UTC)))
    }
}
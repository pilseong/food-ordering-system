package net.philipheur.food_ordering_system.payment_service

import net.philipheur.food_ordering_system.payment_service.domain.core.PaymentDomainServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BeanConfiguration {

    @Bean
    open fun paymentDomainService() = PaymentDomainServiceImpl()
}
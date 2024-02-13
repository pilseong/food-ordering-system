package net.philipheur.food_ordering_system.customer_service

import net.philipheur.food_ordering_system.customer_service.domain.core.CustomerDomainServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class BeanConfiguration {
    @Bean
    open fun customerDomainService() = CustomerDomainServiceImpl()
}
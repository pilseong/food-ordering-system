package net.philipheur.food_ordering_system.order_service

import net.philipheur.food_ordering_system.order_service.dataaccess.customer.adapter.CustomerRepositoryImpl
import net.philipheur.food_ordering_system.order_service.dataaccess.customer.repository.CustomerJpaRepository
import net.philipheur.food_ordering_system.order_service.domain.core.OrderDomainServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class BeanConfiguration {

    @Bean
    open fun orderDomainService() = OrderDomainServiceImpl()
}
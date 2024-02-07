package net.philipheur.food_ordering_system.restaurant_service

import net.philipheur.food_ordering_system.restaurant_service.domain.core.RestaurantDomainServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BeanConfiguration {
    @Bean
    open fun restaurantDomainService() = RestaurantDomainServiceImpl()
}
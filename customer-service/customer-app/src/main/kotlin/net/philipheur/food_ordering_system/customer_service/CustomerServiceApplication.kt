package net.philipheur.food_ordering_system.customer_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(
    basePackages = [
        "net.philipheur.food_ordering_system.customer_service.dataaccess",
    ]
)
@EntityScan(
    basePackages = [
        "net.philipheur.food_ordering_system.customer_service.dataaccess",
    ]
)
@ConfigurationPropertiesScan(
    basePackages = [
        "net.philipheur.food_ordering_system"
    ]
)
@SpringBootApplication(
    scanBasePackages = [
        "net.philipheur.food_ordering_system"
    ]
)
open class CustomerServiceApplication

fun main() {
    runApplication<CustomerServiceApplication>()
}
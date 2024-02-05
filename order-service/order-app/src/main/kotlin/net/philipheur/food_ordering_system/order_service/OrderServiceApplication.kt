package net.philipheur.food_ordering_system.order_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(
    basePackages = [
        "net.philipheur.food_ordering_system.order_service.dataaccess",
        "net.philipheur.food_ordering_system.common.dataaccess",
    ]
)
@EntityScan(
    basePackages = [
        "net.philipheur.food_ordering_system.order_service.dataaccess",
        "net.philipheur.food_ordering_system.common.dataaccess",
    ]
)
@SpringBootApplication(
    scanBasePackages = [
        "net.philipheur.food_ordering_system"
    ]
)
@ConfigurationPropertiesScan(
    basePackages = [
        "net.philipheur.food_ordering_system"
    ]
)
open class OrderServiceApplication


fun main() {
//    val applicationContext =
    runApplication<OrderServiceApplication>()
//    applicationContext.beanDefinitionNames.forEach { println(it) }
}
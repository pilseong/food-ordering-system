package net.philipheur.food_ordering_system.customer_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["net.philipheur.food_ordering_system"])
class CustomerServiceApplication

fun main() {
    runApplication<CustomerServiceApplication>()
}
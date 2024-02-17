package net.philipheur.food_ordering_system.config_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
open class ConfigServer
fun main() {
    runApplication<ConfigServer>()
}
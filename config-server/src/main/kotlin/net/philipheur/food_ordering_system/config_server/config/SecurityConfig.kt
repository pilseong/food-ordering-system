package net.philipheur.food_ordering_system.config_server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
open class SecurityConfig {
    @Bean
    open fun securityFilterChain(
        httpSecurity: HttpSecurity
    ): SecurityFilterChain {
        return httpSecurity
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/actuator/**").permitAll();
                it.requestMatchers("/encrypt/**").permitAll();
                it.requestMatchers("/decrypt/**").permitAll();
                it.anyRequest().authenticated();
            }
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
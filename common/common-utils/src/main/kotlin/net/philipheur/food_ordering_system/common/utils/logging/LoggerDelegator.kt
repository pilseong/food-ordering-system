package net.philipheur.food_ordering_system.common.utils.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggerDelegator {
    private var logger: Logger? = null
    operator fun getValue(thisRef: Any?, property: Any?) =
        logger ?: LoggerFactory.getLogger(thisRef?.javaClass)!!
}
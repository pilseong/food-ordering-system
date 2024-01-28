/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 * For more detailed information on multi-project builds, please refer to https://docs.gradle.org/8.5/userguide/building_swift_projects.html in the Gradle documentation.
 */

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "food-ordering-system"
include(":common-domain")
include(":common-controller")
include(":order-domain-core")
include(":order-domain-application-service")
include(":order-dataaccess")
include(":order-controller")
include(":order-app")
include(":kafka-config-data")
include(":kafka-model")
include(":order-messaging")
include(":kafka-producer")


project(":common-domain").projectDir =
    file("common/common-domain")
project(":common-controller").projectDir =
    file("common/common-controller")
project(":order-domain-core").projectDir =
    file("order-service/order-domain/order-domain-core")
project(":order-domain-application-service").projectDir =
    file("order-service/order-domain/order-domain-application-service")
project(":order-dataaccess").projectDir =
    file("order-service/order-dataaccess")
project(":order-controller").projectDir =
    file("order-service/order-controller")
project(":order-messaging").projectDir =
    file("order-service/order-messaging")
project(":order-app").projectDir =
    file("order-service/order-app")
project(":kafka-config-data").projectDir =
    file("infrastructure/kafka/kafka-config-data")
project(":kafka-model").projectDir =
    file("infrastructure/kafka/kafka-model")
project(":kafka-producer").projectDir =
    file("infrastructure/kafka/kafka-producer")
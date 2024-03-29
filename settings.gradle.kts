rootProject.name = "exchange"
include("exchange-deploy")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("spring.boot.starter.web", "org.springframework.boot:spring-boot-starter-web:3.0.1")
            library("spring.boot.starter.parent", "org.springframework.boot:spring-boot-starter-parent:3.0.1")
            library("spring.websocket", "org.springframework:spring-websocket:6.0.3")
            library("spring.messaging", "org.springframework:spring-messaging:6.0.3")
            library("spring.kafka", "org.springframework.kafka:spring-kafka:3.0.1")
            library("tyrus.standalone.client", "org.glassfish.tyrus.bundles:tyrus-standalone-client:1.18")
            library("javax.json.api", "javax.json:javax.json-api:1.1.4")
            library("javax.json", "org.glassfish:javax.json:1.1.4")
            library("javafaker", "com.github.javafaker:javafaker:1.0.2")
            library("common.api", "com.herron.exchange:common-api:1.0.0-SNAPSHOT")
            library("common", "com.herron.exchange:common:1.0.0-SNAPSHOT")
            library("org.springdoc", "org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.0")
        }

        create("testlibs") {
            library("spring.boot.starter.test", "org.springframework.boot:spring-boot-starter-test:3.0.1")
            library("junit.jupiter.api", "org.junit.jupiter:junit-jupiter-api:5.8.1")
            library("junit.jupiter.engine", "org.junit.jupiter:junit-jupiter-engine:5.8.1")
            library("spring.kafka.test", "org.springframework.kafka:spring-kafka:3.0.1")
        }
    }
}
include("exchange-server")

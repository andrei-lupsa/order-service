package com.polarbookshop.orderservice.testcontainers;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;

public interface TestContainers {

    @Container
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.4")
            .withDatabaseName("polardb_order")
            .withUsername("user")
            .withPassword("password")
            .withMinimumRunningDuration(Duration.ofSeconds(5));

}

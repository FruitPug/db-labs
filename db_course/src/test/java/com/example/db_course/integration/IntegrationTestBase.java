package com.example.db_course.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.TimeZone;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres")
                    .withDatabaseName("db_course_test")
                    .withUsername("user")
                    .withPassword("pass")
                    .withEnv("TZ", "UTC")
                    .withEnv("PGTZ", "UTC");

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        postgres.start();
    }

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }
}

package com.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// =============================================================
// Test de démarrage du contexte Spring
// Utilisé dans le pipeline CI/CD GitHub Actions (Job 1)
// Utilise H2 en mémoire → pas besoin de MySQL en CI
// =============================================================

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.thymeleaf.cache=false"
})
class LibraryManagementApplicationTests {

    @Test
    void contextLoads() {
        // Vérifie que le contexte Spring Boot démarre correctement
        // Si une dépendance est mal configurée, ce test échoue
    }
}
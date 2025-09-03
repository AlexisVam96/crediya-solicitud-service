package co.com.crediya.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebFluxTest
@Import(SecurityHeadersConfig.class)
@ContextConfiguration(classes = {SecurityHeadersConfig.class, SecurityHeadersConfigTest.DummyController.class})
class SecurityHeadersConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @RestController
    static class DummyController {
        @GetMapping("/test")
        public String test() {
            return "ok";
        }
    }

    @Test
    void shouldAddSecurityHeaders() {
        webTestClient.get().uri("/test")
                .exchange()
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin")
                .expectStatus().isOk();
    }
}
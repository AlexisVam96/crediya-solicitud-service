package co.com.crediya.api.exception;


import co.com.crediya.api.config.CorsConfig;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@WebFluxTest(controllers = GlobalExceptionHandlerTest.DummyController.class)
@Import({GlobalExceptionHandler.class, GlobalErrorAttributes.class, CorsConfig.class})
@ContextConfiguration(classes = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.DummyController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @TestConfiguration
    static class WebPropertiesConfig {
        @Bean
        public WebProperties webProperties() {
            return new WebProperties();
        }
    }

    @RestController
    static class DummyController {
        @GetMapping("/error-validation")
        public void errorValidation() throws LoanApplicationCustomerException {
            throw new LoanApplicationCustomerException("Test exception validation", ErrorType.VALIDATION);
        }

        @GetMapping("/error-notfound")
        public void errorNotFound() throws LoanApplicationCustomerException {
            throw new LoanApplicationCustomerException("Test exception not found", ErrorType.NOT_FOUND);
        }

        @GetMapping("/error-auth")
        public void errorAuth() throws LoanApplicationCustomerException {
            throw new LoanApplicationCustomerException("Test exception authorization", ErrorType.AUTH);
        }

        @GetMapping("/error-system")
        public void errorSystem() throws LoanApplicationCustomerException {
            throw new LoanApplicationCustomerException("Test exception system", ErrorType.SYSTEM);
        }
    }

    @Test
    void handleUserCustomException_shouldReturnCustomErrorValidation() {
        webTestClient.get()
                .uri("/error-validation")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/error-validation")
                .jsonPath("$.message").isEqualTo("Test exception validation")
                .jsonPath("$.type").isEqualTo("VALIDATION");
    }

    @Test
    void handleUserCustomException_shouldReturnCustomErrorNotFound() {
        webTestClient.get()
                .uri("/error-notfound")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/error-notfound")
                .jsonPath("$.message").isEqualTo("Test exception not found")
                .jsonPath("$.type").isEqualTo("NOT_FOUND");
    }

    @Test
    void handleUserCustomException_shouldReturnCustomErrorAuth() {
        webTestClient.get()
                .uri("/error-auth")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/error-auth")
                .jsonPath("$.message").isEqualTo("Test exception authorization")
                .jsonPath("$.type").isEqualTo("AUTH");
    }

    @Test
    void handleUserCustomException_shouldReturnCustomErrorSystem() {
        webTestClient.get()
                .uri("/error-system")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.path").isEqualTo("/error-system")
                .jsonPath("$.message").isEqualTo("Test exception system")
                .jsonPath("$.type").isEqualTo("SYSTEM");
    }
}
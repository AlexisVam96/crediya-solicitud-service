package co.com.crediya.consumer;

import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateway.ExternalUserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements ExternalUserGateway {
    private final WebClient client;

    @CircuitBreaker(name = "findByDocumentNumber", fallbackMethod = "testGetUserOk")
    public Mono<User> findByDocumentNumber(String documentNumber) {
        return client
                .get()
                .uri("/api/v1/user/{documentNumber}", documentNumber)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        err -> Mono.error(new LoanApplicationCustomerException("Error ms user not found", ErrorType.NOT_FOUND)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        err -> Mono.error(new LoanApplicationCustomerException("Error ms user unavailable", ErrorType.SYSTEM)))
                .bodyToMono(User.class);
    }

    public Mono<User> testGetUserOk(String documentNumber, Throwable throwable) {
        User defaultUser = User.builder()
                .documentNumber(documentNumber)
                .name("Default")
                .email("default@example.com")
                .role("USER")
                .build();
        return Mono.just(defaultUser);
    }
}

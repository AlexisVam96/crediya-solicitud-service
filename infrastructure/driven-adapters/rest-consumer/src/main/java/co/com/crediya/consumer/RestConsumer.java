package co.com.crediya.consumer;

import co.com.crediya.consumer.config.ErrorResponse;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateway.ExternalUserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements ExternalUserGateway {
    private final WebClient client;


    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<User> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(User.class);
    }

    @CircuitBreaker(name = "findByDocumentNumber")
    public Mono<User> findByDocumentNumber(String documentNumber) {
        return client
                .get()
                .uri("/api/v1/user/{documentNumber}", documentNumber)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(error -> Mono.error(
                                new LoanApplicationCustomerException(
                                        error.getMessage(),
                                        mapStatusToType(error.getType())
                                )
                        ))
                )
                .bodyToMono(User.class);
    }

    private ErrorType mapStatusToType(String type) {
        return switch (type) {
            case "VALIDATION" -> ErrorType.VALIDATION;
            case "AUTH" -> ErrorType.AUTH;
            case "NOT_FOUND" -> ErrorType.NOT_FOUND;
            default -> ErrorType.SYSTEM;
        };
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @CircuitBreaker(name = "testPost")
    public Mono<ObjectResponse> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return client
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }
}

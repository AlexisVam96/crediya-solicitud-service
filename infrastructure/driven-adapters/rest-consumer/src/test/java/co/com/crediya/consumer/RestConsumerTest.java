package co.com.crediya.consumer;


import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.LoanApplicationCustomerException;
import co.com.crediya.model.user.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.io.IOException;


public class RestConsumerTest {

    private static RestConsumer restConsumer;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        restConsumer = new RestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate the function findByDocumentNumber.")
    void validateTestGet() {

        String documentNumber = "1234567890";
        User user = new User();
        user.setIdUser(1);
        user.setDocumentNumber(documentNumber);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@crediya.com");

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"documentNumber\" : \"1234567890\"}"));
        var response = restConsumer.findByDocumentNumber(documentNumber);

        StepVerifier.create(response)
                .expectNextMatches(objectResponse -> objectResponse.getDocumentNumber().equals("1234567890"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Fallback returns default User with documentNumber and email")
    void testGetOkFallbackReturnsDefaultUser() {
        RestConsumer restConsumer = new RestConsumer(null); // WebClient not needed for this test
        String documentNumber = "123456";
        Throwable throwable = new LoanApplicationCustomerException("Simulated error", ErrorType.SYSTEM);

        Mono<User> result = restConsumer.testGetOk(documentNumber, throwable);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getDocumentNumber().equals(documentNumber)
                        && user.getEmail().equals("example@crediya.com"))
                .verifyComplete();
    }


    @Test
    @DisplayName("Should return NOT_FOUND exception for 4xx response")
    void shouldReturnNotFoundExceptionFor4xxResponse() {
        String documentNumber = "1234567890";
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(restConsumer.findByDocumentNumber(documentNumber))
                .expectErrorMatches(throwable ->
                        throwable instanceof LoanApplicationCustomerException &&
                                ((LoanApplicationCustomerException) throwable).getType() == ErrorType.NOT_FOUND)
                .verify();
    }

    @Test
    @DisplayName("Should return SYSTEM exception for 5xx response")
    void shouldReturnSystemExceptionFor5xxResponse() {
        String documentNumber = "1234567890";
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(restConsumer.findByDocumentNumber(documentNumber))
                .expectErrorMatches(throwable ->
                        throwable instanceof LoanApplicationCustomerException &&
                                ((LoanApplicationCustomerException) throwable).getType() == ErrorType.SYSTEM)
                .verify();
    }

}
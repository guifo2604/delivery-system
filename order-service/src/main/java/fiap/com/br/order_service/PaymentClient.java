package fiap.com.br.order_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClient {
    private final RestTemplate restTemplate;

    public record PaymentRequest(BigDecimal amount) {}
    public record PaymentResponse(String status, int instance) {}

    @Retryable(maxRetries = 5, delay = 200, multiplier = 2, jitter = 100, maxDelay = 2000)
    public PaymentResponse pay(BigDecimal amount) {
        PaymentResponse response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                new PaymentRequest(amount),
                PaymentResponse.class);
        log.info("Payment approved by instance {}", response.instance());
        return response;
    }
}

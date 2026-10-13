package fiap.com.br.payment_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Random;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final Random random = new Random();

    @Value("${server.port}")
    private int port;

    public record PaymentRequest(BigDecimal amount) {}
    public record PaymentResponse(String status, int instance) {}

    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        if (random.nextBoolean()) {
            log.warn("Payment failed on instance {}", port);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "payment failed");
        }
        log.info("Payment of {} approved on instance {}", request.amount(), port);
        return new PaymentResponse("APPROVED", port);
    }
}
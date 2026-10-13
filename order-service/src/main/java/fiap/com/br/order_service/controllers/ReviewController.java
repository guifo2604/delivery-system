package fiap.com.br.order_service.controllers;

import fiap.com.br.order_service.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    public record ReviewRequest(Long dishId, Integer rating, String comment) {}

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ReviewRequest request) {
        reviewService.publish(request.dishId(), request.rating(), request.comment());
        return ResponseEntity.accepted().build();
    }
}

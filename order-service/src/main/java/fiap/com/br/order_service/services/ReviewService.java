package fiap.com.br.order_service.services;

import fiap.com.br.order_service.config.RabbitConfig;
import fiap.com.br.order_service.exceptions.ApiException;
import fiap.com.br.order_service.models.Dish;
import fiap.com.br.order_service.records.ReviewMessage;
import fiap.com.br.order_service.repositories.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final DishRepository dishRepository;
    private final RabbitTemplate rabbitTemplate;

    public void publish(Long dishId, Integer rating, String comment) {
        if (dishId == null || rating == null || rating < 1 || rating > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dish not found"));

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                new ReviewMessage(dish.getId(), dish.getName(), rating, comment));
    }
}

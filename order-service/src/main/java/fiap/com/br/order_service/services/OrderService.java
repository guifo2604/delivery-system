package fiap.com.br.order_service.services;

import fiap.com.br.order_service.PaymentClient;
import fiap.com.br.order_service.exceptions.ApiException;
import fiap.com.br.order_service.models.CustomerOrder;
import fiap.com.br.order_service.models.Dish;
import fiap.com.br.order_service.repositories.CustomerOrderRepository;
import fiap.com.br.order_service.repositories.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final DishRepository dishRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentClient paymentClient;

    @Transactional
    public CustomerOrder createOrder(Long dishId, Integer quantity) {
        if (dishId == null || quantity == null || quantity < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        Dish dish = dishRepository.findByIdForUpdate(dishId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dish not found"));

        if (dish.getStock() < quantity) {
            throw new ApiException(HttpStatus.CONFLICT, "Dish out of stock");
        }

        dish.setStock(dish.getStock() - quantity);
        BigDecimal total = dish.getPrice().multiply(BigDecimal.valueOf(quantity));

        try {
            paymentClient.pay(total);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Payment service unavailable");
        }
        return orderRepository.save(CustomerOrder.builder()
                .dishId(dishId)
                .quantity(quantity)
                .totalPrice(dish.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .status("CONFIRMED")
                .build());
    }
}

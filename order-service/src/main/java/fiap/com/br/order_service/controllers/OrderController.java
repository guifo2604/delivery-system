package fiap.com.br.order_service.controllers;

import fiap.com.br.order_service.exceptions.ApiException;
import fiap.com.br.order_service.models.CustomerOrder;
import fiap.com.br.order_service.repositories.CustomerOrderRepository;
import fiap.com.br.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CustomerOrderRepository orderRepository;

    public OrderController(OrderService orderService, CustomerOrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    public record OrderRequest(Long dishId, Integer quantity) {}

    @PostMapping
    public ResponseEntity<CustomerOrder> create(@RequestBody OrderRequest request) {
        CustomerOrder order = orderService.createOrder(request.dishId(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public CustomerOrder get(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}

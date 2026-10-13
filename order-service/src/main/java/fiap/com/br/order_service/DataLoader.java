package fiap.com.br.order_service;

import fiap.com.br.order_service.models.Dish;
import fiap.com.br.order_service.repositories.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final DishRepository dishRepository;

    @Override
    public void run(String... args) {
        dishRepository.saveAll(List.of(
                Dish.builder().name("House Burger").description("Brioche bun")
                        .price(new BigDecimal("39.90")).stock(10).build(),
                Dish.builder().name("Margherita Pizza").description("Tomato, mozzarella and basil")
                        .price(new BigDecimal("49.90")).stock(50).build(),
                Dish.builder().name("Veggie Bowl").description("Quinoa, grilled vegetables and hummus")
                        .price(new BigDecimal("34.90")).stock(30).build(),
                Dish.builder().name("Caesar Salad").description("Romaine, parmesan and croutons")
                        .price(new BigDecimal("29.90")).stock(40).build(),
                Dish.builder().name("Chocolate Brownie").description("Warm brownie with ice cream")
                        .price(new BigDecimal("19.90")).stock(25).build()
        ));
    }
}
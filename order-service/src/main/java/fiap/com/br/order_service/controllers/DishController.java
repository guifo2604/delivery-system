package fiap.com.br.order_service.controllers;

import fiap.com.br.order_service.exceptions.ApiException;
import fiap.com.br.order_service.models.Dish;
import fiap.com.br.order_service.repositories.DishRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishRepository dishRepository;

    @GetMapping
    public List<Dish> list() {
        return dishRepository.findAll();
    }

    @GetMapping("/{id}")
    public Dish get(@PathVariable Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Dish not found"));
    }
}
package fiap.com.br.review_service.controller;

import fiap.com.br.review_service.repositories.ReviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewSummaryRepository repository;

    public record RankingItem(Long dishId, String dishName, double average, long count) {}

    @GetMapping("/ranking")
    public List<RankingItem> ranking() {
        return repository.findAll().stream()
                .map(s -> new RankingItem(
                        s.getDishId(),
                        s.getDishName(),
                        Math.round((double) s.getTotalRating() / s.getReviewCount() * 100) / 100.0,
                        s.getReviewCount()))
                .sorted(Comparator.comparingDouble(RankingItem::average).reversed())
                .toList();
    }
}

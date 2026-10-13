package fiap.com.br.review_service.buffer;

import fiap.com.br.review_service.config.RabbitConfig;
import fiap.com.br.review_service.models.ReviewSummary;
import fiap.com.br.review_service.records.ReviewMessage;
import fiap.com.br.review_service.repositories.ReviewSummaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewBuffer {

    private static class Accumulator {
        private final String dishName;
        private long sum;
        private long count;

        Accumulator(String dishName) { this.dishName = dishName; }

        void add(int rating) { sum += rating; count++; }
    }

    private final ConcurrentHashMap<Long, Accumulator> buffer = new ConcurrentHashMap<>();
    private final ReviewSummaryRepository repository;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receive(ReviewMessage message) {
        buffer.compute(message.dishId(), (id, acc) -> {
            if (acc == null) acc = new Accumulator(message.dishName());
            acc.add(message.rating());
            return acc;
        });
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void flush() {
        for (Long dishId : new ArrayList<>(buffer.keySet())) {
            Accumulator acc = buffer.remove(dishId);
            if (acc == null) continue;

            ReviewSummary summary = repository.findById(dishId)
                    .orElseGet(() -> new ReviewSummary(dishId, acc.dishName, 0, 0));
            summary.setTotalRating(summary.getTotalRating() + acc.sum);
            summary.setReviewCount(summary.getReviewCount() + acc.count);
            repository.save(summary);

            log.info("Flushed {} reviews for dish {}", acc.count, dishId);
        }
    }
}

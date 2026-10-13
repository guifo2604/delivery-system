package fiap.com.br.review_service.repositories;

import fiap.com.br.review_service.models.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewSummaryRepository extends JpaRepository<ReviewSummary, Long> {}

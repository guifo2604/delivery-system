package fiap.com.br.review_service.models;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummary {
    @Id
    private Long dishId;
    private String dishName;
    private long totalRating;
    private long reviewCount;
}

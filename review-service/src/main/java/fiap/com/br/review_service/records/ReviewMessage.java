package fiap.com.br.review_service.records;

public record ReviewMessage(Long dishId, String dishName, int rating, String comment) {}
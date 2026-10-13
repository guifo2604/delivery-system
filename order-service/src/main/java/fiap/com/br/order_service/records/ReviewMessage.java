package fiap.com.br.order_service.records;

public record ReviewMessage(Long dishId, String dishName, int rating, String comment) {}

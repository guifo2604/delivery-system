package fiap.com.br.order_service.services;

import fiap.com.br.order_service.repositories.DishRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final String SYSTEM_TEMPLATE = """
        You are a friendly waiter at a restaurant. Answer in Portuguese, in short replies (at most 3 sentences).
        Use ONLY the menu below to answer about dishes, ingredients, prices, availability and suggestions.
        A dish with stock 0 is unavailable: do not recommend it.
        If the question is not about this restaurant or its menu, politely refuse and steer the conversation back to the restaurant.

        MENU:
        {menu}
        """;

    private final ChatClient chatClient;
    private final DishRepository dishRepository;

    public ChatService(ChatClient.Builder builder, DishRepository dishRepository) {
        this.chatClient = builder.build();
        this.dishRepository = dishRepository;
    }

    public String ask(String question) {
        String menu = dishRepository.findAll().stream()
                .map(d -> "- %s | price: R$ %s | stock: %d | %s"
                        .formatted(d.getName(), d.getPrice(), d.getStock(), d.getDescription()))
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system(s -> s.text(SYSTEM_TEMPLATE).param("menu", menu))
                .user(question)
                .call()
                .content();
    }
}
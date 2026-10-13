package fiap.com.br.order_service.controllers;

import fiap.com.br.order_service.exceptions.ApiException;
import fiap.com.br.order_service.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {
    private final ChatService chatService;

    public record AssistantRequest(String question) {}
    public record AssistantResponse(String answer) {}

    @PostMapping
    public AssistantResponse ask(@RequestBody AssistantRequest request) {
        if (request.question() == null || request.question().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Question must not be blank");
        }
        try {
            return new AssistantResponse(chatService.ask(request.question()));
        } catch (Exception e) {
            log.error("Assistant call failed", e);
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Assistant unavailable");
        }
    }
}
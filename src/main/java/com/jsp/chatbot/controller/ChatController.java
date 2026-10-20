package com.jsp.chatbot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.chatbot.service.ChatService;

@RestController
@RequestMapping("/api")
public class ChatController {
    private static final int MAX_MESSAGE_LENGTH = 8_000;

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody(required = false) ChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse(null, "Enter a message before sending it."));
        }
        if (request.message().length() > MAX_MESSAGE_LENGTH) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse(null, "Messages must be 8,000 characters or fewer."));
        }

        try {
            return ResponseEntity.ok(new ChatResponse(chatService.askQuestion(request.message()), null));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ChatResponse(null, ex.getMessage()));
        } catch (RuntimeException ex) {
            String details = ex.getMessage();
            String error = details == null || details.isBlank()
                    ? "Gemini request failed. Check the server log for details."
                    : "Gemini request failed: " + details;
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new ChatResponse(null, error));
        }
    }

    public record ChatRequest(String message) {}

    public record ChatResponse(String reply, String error) {}
}

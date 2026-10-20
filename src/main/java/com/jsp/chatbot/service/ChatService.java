package com.jsp.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.GenerateContentResponse;

@Service
public class ChatService {
    private static final int MAX_ATTEMPTS = 3;

    private final String apiKey;
    private volatile Client client;

    public ChatService(@Value("${google.apikey}") String apikey) {
        this.apiKey = apikey;
    }

    public String askQuestion(String userText) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Set google.apikey in application.properties before calling /api/chat.");
        }

        String prompt = userText == null ? "" : userText;
        GenerateContentResponse response = generateWithRetry(prompt);
        String answer = response != null ? response.text() : null;
        return answer == null || answer.isBlank() ? "Gemini returned an empty response." : answer;
    }

    private GenerateContentResponse generateWithRetry(String prompt) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return getClient().models.generateContent("gemini-3.8-flash", prompt, null);
            } catch (ApiException ex) {
                boolean temporaryFailure = ex.code() == 429 || ex.code() == 503;
                if (!temporaryFailure || attempt == MAX_ATTEMPTS) {
                    throw ex;
                }

                try {
                    Thread.sleep(1_000L * attempt);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Gemini retry was interrupted.", interrupted);
                }
            }
        }
        throw new IllegalStateException("Gemini did not return a response.");
    }

    private Client getClient() {
        Client currentClient = client;
        if (currentClient == null) {
            synchronized (this) {
                currentClient = client;
                if (currentClient == null) {
                    currentClient = Client.builder().apiKey(apiKey).build();
                    client = currentClient;
                }
            }
        }
        return currentClient;
    }
}

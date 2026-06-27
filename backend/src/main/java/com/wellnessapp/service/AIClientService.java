package com.wellnessapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Client service for calling DeepSeek Chat Completions.
 * Falls back gracefully when the AI service is unavailable.
 *
 * @author WellnessApp Team
 */
@Service
public class AIClientService {

    private static final Logger log = LoggerFactory.getLogger(AIClientService.class);
    private static final String DEEPSEEK_CHAT_URL = "https://api.deepseek.com/chat/completions";
    private static final String DEEPSEEK_MODEL = "deepseek-v4-flash";

    private final RestTemplate restTemplate;
    private final String apiKey;

    private static final String SYSTEM_PROMPT = """
            You are a friendly and knowledgeable wellness assistant named "WellBot".
            Your role is to provide helpful, evidence-based advice on health and wellness topics.

            Guidelines:
            - ONLY answer questions related to health, wellness, fitness, nutrition, sleep, mental health, and lifestyle.
            - If asked about topics outside wellness, politely redirect the user back to wellbeing.
            - Use the same language as the user.
            - Keep responses concise (2-5 sentences) and actionable.
            - Be encouraging and supportive.
            - Do NOT provide medical diagnoses. Always recommend consulting a healthcare professional for medical concerns.
            - If you don't know something, be honest about it.
            """;

    public AIClientService(@Value("${deepseek.api-key:}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
    }

    /**
     * Get a chat response from DeepSeek.
     * Throws RuntimeException if the service is unavailable (caller should fall back).
     */
    public String getChatResponse(String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("DeepSeek API key is not configured");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "model", DEEPSEEK_MODEL,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "thinking", Map.of("type", "disabled"),
                    "max_tokens", 300,
                    "temperature", 0.7,
                    "stream", false
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(DEEPSEEK_CHAT_URL, request, Map.class);

            return extractReply(response);
        } catch (Exception e) {
            log.error("DeepSeek API call failed: {}", e.getMessage());
            throw new RuntimeException("DeepSeek API unavailable: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String extractReply(ResponseEntity<Map> response) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Unexpected DeepSeek response status");
        }

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.getBody().get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("DeepSeek response did not contain choices");
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null || message.get("content") == null) {
            throw new RuntimeException("DeepSeek response did not contain message content");
        }

        return message.get("content").toString().trim();
    }
}
